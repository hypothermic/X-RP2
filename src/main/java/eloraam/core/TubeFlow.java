/* X-RP - decompiled with CFR */
package eloraam.core;

import dreadend.RP2.mutithread.RP2ThreadManager;
import net.minecraft.server.*;

import java.util.*;

public abstract class TubeFlow {

    public List<TubeItem> contents = Collections.synchronizedList(new LinkedList()); // X-RP: added type decl
    public boolean changed = false;

    public void add(TubeItem arg0) {
        arg0.progress = 0;
        arg0.scheduled = false;
        this.contents.add(arg0);
    }

    public abstract TileEntity getParent();

    public boolean handleItem(TubeItem arg0, TubeScheduleContext arg1) {
        return false;
    }

    public void onRemove() {
        TileEntity arg0 = this.getParent();
        for (TubeItem arg2 : this.contents) {
            if (arg2 == null || arg2.item.count <= 0)
                continue;
            CoreLib.dropItem(arg0.world, arg0.x, arg0.y, arg0.z, arg2.item);
        }
    }

    public void readFromNBT(NBTTagCompound arg0) {
        NBTTagList arg1 = arg0.getList("Items");
        if (arg1.size() > 0) {
            this.contents = Collections.synchronizedList(new LinkedList<TubeItem>()); // X-RP: replace gen type decl "E" -> "TubeItem"
            int arg2 = 0;
            while (arg2 < arg1.size()) {
                NBTTagCompound arg3 = (NBTTagCompound) arg1.get(arg2);
                this.contents.add(TubeItem.newFromNBT(arg3));
                ++arg2;
            }
        }
    }

    public abstract boolean schedule(TubeItem var1, TubeScheduleContext var2);

    public boolean update() {
        boolean arg0 = false;
        if (this.contents.size() == 0) {
            return false;
        }
        final TubeScheduleContext arg1 = new TubeScheduleContext(this.getParent());
        arg1.tii = this.contents.iterator();
        Iterator<TubeItem> T = new LinkedList<TubeItem>(this.contents).iterator(); // X-RP: replace both gen type decls "E" -> "TubeItem"
        while (T.hasNext()) {
            try {
                TubeItem arg2 = (TubeItem) T.next();
                arg2.progress = (short) (arg2.progress + arg2.power + 16);
                if (arg2.progress < 128)
                    continue;
                if (arg2.power > 0) {
                    arg2.power = (short) (arg2.power - 1);
                }
                arg0 = true;
                if (!arg2.scheduled) {
                    if (this.schedule(arg2, arg1))
                        continue;
                    this.contents.remove(arg2);
                    continue;
                }
                this.contents.remove(arg2);
                if (CoreProxy.isClient(arg1.world))
                    continue;
                arg1.tir.add(arg2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (CoreProxy.isClient(arg1.world)) {
            return arg0;
        }
        RP2ThreadManager.tubeThread.submit(new Runnable() {

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                try {
                    boolean arg0 = false;
                    int i = 0;
                    while (i < arg1.tir.size()) {
                        TubeItem arg3 = (TubeItem) arg1.tir.get(i);
                        if (arg3.side >= 0 && (arg1.cons & 1 << arg3.side) != 0) {
                            arg1.dest = arg1.wc.copy();
                            arg1.dest.step(arg3.side);
                            World world = arg1.world;
                            synchronized (world) {
                                ITubeConnectable arg4 = (ITubeConnectable) CoreLib.getTileEntity((IBlockAccess) arg1.world, arg1.dest, ITubeConnectable.class);
                                if (arg4 instanceof ITubeFlow) {
                                    ITubeFlow arg5 = (ITubeFlow) arg4;
                                    arg5.addTubeItem(arg3);
                                } else if (!(arg4 != null && arg4.tubeItemEnter((arg3.side ^ 1) & 63, arg3.mode, arg3) || TubeFlow.this.handleItem(arg3, arg1))) {
                                    arg3.progress = 0;
                                    arg3.scheduled = false;
                                    arg3.mode = 2;
                                    TubeFlow.this.contents.add(arg3);
                                }
                            }
                        } else if (arg1.cons == 0) {
                            MachineLib.ejectItem(arg1.world, arg1.wc, arg3.item, 1);
                        } else {
                            arg3.side = (byte) Integer.numberOfTrailingZeros(arg1.cons);
                            arg3.progress = 128;
                            arg3.scheduled = false;
                            TubeFlow.this.contents.add(arg3);
                            arg0 = true;
                        }
                        ++i;
                    }
                    if (!TubeFlow.this.changed) {
                        TubeFlow.this.changed = arg0;
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
        return false;
    }

    public void writeToNBT(NBTTagCompound arg0) {
        NBTTagList arg1 = new NBTTagList();
        if (this.contents != null) {
            int i = 0;
            while (i < this.contents.size()) {
                try {
                    TubeItem arg4 = (TubeItem) this.contents.get(i);
                    NBTTagCompound arg2 = new NBTTagCompound();
                    arg4.writeToNBT(arg2);
                    arg1.add((NBTBase) arg2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ++i;
            }
        }
        arg0.set("Items", (NBTBase) arg1);
    }

    public static class TubeScheduleContext {

        public World world;
        public WorldCoord wc;
        public int cons;
        public ArrayList tir = new ArrayList();
        public Iterator tii;
        public WorldCoord dest = null;

        public TubeScheduleContext(TileEntity arg0) {
            this.world = arg0.world;
            this.wc = new WorldCoord(arg0);
            this.cons = TubeLib.getConnections((IBlockAccess) this.world, this.wc.x, this.wc.y, this.wc.z);
        }
    }

}
