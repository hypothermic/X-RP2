/* X-RP - decompiled with CFR */
package eloraam.machine;

import dreadend.RP2.mutithread.RP2ThreadManager;
import eloraam.core.*;
import net.minecraft.server.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

//import dreadend.RP2.mutithread.ScheduledTubeRunnable;

public class TileTube extends TileCovered implements ITubeFlow, IPaintable {

    public TubeFlow flow;
    private byte lastDir;
    byte paintColor;
    private boolean hasChanged;
    private static LinkedBlockingQueue<TileTube> tileTubeQueue = new LinkedBlockingQueue<>();

    static {
        new Thread() {

            @Override
            public void run() {
                Thread.currentThread().setName("RP2-TubeMT");
                do {
                    try {
                        do {
                            if (!TileTube.tileTubeQueue.isEmpty()) {
                                TileTube tube = TileTube.tileTubeQueue.poll();
                                tube.processUpdate();
                            } else {
                                Thread.sleep(50);
                            }
                        } while (true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (true);
            }
        }.start();
    }

    public TileTube() {
        this.flow = new TubeFlow() {

            @Override
            public TileEntity getParent() {
                return TileTube.this;
            }

            @Override
            public boolean handleItem(TubeItem arg0, TubeFlow.TubeScheduleContext arg1) {
                return MachineLib.addToInventory(TileTube.this.world, arg0.item, arg1.dest, (arg0.side ^ 1) & 63);
            }

            @Override
            public boolean schedule(TubeItem arg0, TubeFlow.TubeScheduleContext arg1) {
                arg0.scheduled = true;
                arg0.progress = 0;
                int arg2 = arg1.cons & ~(1 << arg0.side);
                if (arg2 == 0) {
                    return true;
                }
                if (Integer.bitCount(arg2) == 1) {
                    arg0.side = (byte) Integer.numberOfTrailingZeros(arg2);
                    return true;
                }
                if (CoreProxy.isClient(TileTube.this.world)) {
                    return false;
                }
                if (arg0.mode != 3) {
                    arg0.mode = 1;
                }
                RP2ThreadManager.tubeThread.submit(new ScheduledTubeRunnable(arg0, arg1, arg2));
                return true;
            }

            // X-RP2: removed ScheduledTubeRunnable static mthd

            class ScheduledTubeRunnable implements Runnable {

                public TubeItem tube;
                public TubeFlow.TubeScheduleContext context;
                public int side;

                public ScheduledTubeRunnable(TubeItem arg0, TubeFlow.TubeScheduleContext arg1, int arg2) {
                    tube = arg0;
                    context = arg1;
                    side = arg2;
                }

                public void run() {
                    tube.side = ((byte) TubeLib.findRoute(context.world, context.wc, tube, side, tube.mode, lastDir));

                    if (tube.side >= 0) {
                        int arg3 = side & ((2 << lastDir) - 1 ^ 0xFFFFFFFF);
                        if (arg3 == 0) {
                            arg3 = side;
                        }

                        if (arg3 == 0) {
                            lastDir = 0;
                        } else {
                            lastDir = ((byte) Integer.numberOfTrailingZeros(arg3));
                        }
                    } else {
                        tube.side = ((byte) TubeLib.findRoute(context.world, context.wc, tube, context.cons, 2));
                        if (tube.side >= 0) {
                            tube.mode = 2;
                            return;
                        }

                        if (tube.mode == 3) {
                            tube.side = ((byte) TubeLib.findRoute(context.world, context.wc, tube, context.cons, 1));
                            tube.mode = 1;
                        }

                        if (tube.side < 0) {
                            tube.side = lastDir;
                            int arg3 = side & ((2 << lastDir) - 1 ^ 0xFFFFFFFF);
                            if (arg3 == 0) {
                                arg3 = side;
                            }

                            if (arg3 == 0) {
                                lastDir = 0;
                            } else {
                                lastDir = ((byte) Integer.numberOfTrailingZeros(arg3));
                            }
                        }
                    }
                }
            }
        };
        this.lastDir = 0;
        this.paintColor = 0;
        this.hasChanged = false;
    }

    // load from NBT
    @Override
    public void a(NBTTagCompound tag) {
        super.a(tag);
        this.flow.readFromNBT(tag);
        this.lastDir = tag.getByte("lDir");
        this.paintColor = tag.getByte("pCol");
    }

    @Override
    public void addHarvestContents(ArrayList harvestList) {
        super.addHarvestContents(harvestList);
        harvestList.add(new ItemStack(RedPowerBase.blockMicro.id, 1, this.getExtendedID() << 8));
    }

    @Override
    public void addTubeItem(TubeItem item) {
        item.side = (byte) (item.side ^ 1);
        this.flow.add(item);
        this.hasChanged = true;
        this.dirtyBlock();
    }

    // save to NBT
    @Override
    public void b(NBTTagCompound tag) {
        super.b(tag);
        this.flow.writeToNBT(tag);
        tag.setByte("lDir", this.lastDir);
        tag.setByte("pCol", this.paintColor);
    }

    @Override
    public boolean blockEmpty() {
        return false;
    }

    @Override
    public boolean canRouteItems() {
        return true;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public int getBlockID() {
        return RedPowerBase.blockMicro.id;
    }

    @Override
    public int getExtendedID() {
        return 8;
    }

    @Override
    public int getPartsMask() {
        return this.CoverSides | 536870912;
    }

    // get player strength vs this tube
    @Override
    public float getPartStrength(EntityHuman attacker, int toolStrength) {
        BlockMachine arg2 = RedPowerMachine.blockMachine;
        return toolStrength == 29 ? attacker.getCurrentPlayerStrVsBlock((Block) arg2, 0) / (arg2.m() * 30.0f) : super.getPartStrength(attacker, toolStrength);
    }

    @Override
    public int getSolidPartsMask() {
        return this.CoverSides | 536870912;
    }

    @Override
    public int getTubeConClass() {
        return this.paintColor;
    }

    @Override
    public int getTubeConnectableSides() {
        int arg0 = 63;
        int arg1 = 0;
        while (arg1 < 6) {
            if ((this.CoverSides & 1 << arg1) > 0 && this.Covers[arg1] >> 8 < 3) {
                arg0 &= ~(1 << arg1);
            }
            ++arg1;
        }
        return arg0;
    }

    @Override
    public TubeFlow getTubeFlow() {
        return this.flow;
    }

    @Override
    public void handlePacket(Packet211TileDesc arg0) {
        try {
            this.readFromPacket(arg0);
        } catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public void onBlockNeighborChange(int arg0) {
    }

    @Override
    public void onHarvestPart(EntityHuman arg0, int arg1) {
        if (arg1 == 29) {
            CoreLib.dropItem(this.world, this.x, this.y, this.z, new ItemStack(RedPowerBase.blockMicro.id, 1, this.getExtendedID() << 8));
            this.flow.onRemove();
            if (this.CoverSides > 0) {
                this.replaceWithCovers();
            } else {
                this.deleteBlock();
            }
        } else {
            super.onHarvestPart(arg0, arg1);
        }
    }

    @Override
    public void q_() {
        tileTubeQueue.add(this);
    }

    @Override
    public void processUpdate() {
        if (this.flow.update()) {
            this.hasChanged = true;
        }
        if (this.hasChanged || this.flow.changed) {
            this.hasChanged = false;
            if (CoreProxy.isServer()) {
                this.sendItemUpdate();
            }
            this.dirtyBlock();
        }
    }

    @Override
    protected void readFromPacket(Packet211TileDesc packet) throws IOException {
        if (packet.subId == 10) {
            this.flow.contents.clear();
            int arg1 = (int) packet.getUVLC();
            int arg2 = 0;
            while (arg2 < arg1) {
                this.flow.contents.add(TubeItem.newFromPacket(packet));
                ++arg2;
            }
        } else {
            super.readFromPacket(packet);
            this.paintColor = (byte) packet.getByte();
        }
    }

    protected void sendItemUpdate() {
        Packet211TileDesc packet = new Packet211TileDesc();
        packet.subId = 10;
        packet.xCoord = this.x;
        packet.yCoord = this.y;
        packet.zCoord = this.z;
        int arg1 = this.flow.contents.size();
        if (arg1 > 6) {
            arg1 = 6;
        }
        packet.addUVLC(arg1);
        try {
            int maxmin = Math.min(5, this.flow.contents.size());
            int arg3 = 0;
            while (arg3 < maxmin) {
                TubeItem arg4 = (TubeItem) this.flow.contents.get(arg3);
                arg4.writeToPacket(packet);
                ++arg3;
            }
            packet.encode();
            CoreProxy.sendPacketToPosition(packet, this.x, this.z);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPartBounds(BlockMultipart blockMultipart, int arg1) {
        if (arg1 == 29) {
            blockMultipart.a(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
        } else {
            super.setPartBounds(blockMultipart, arg1);
        }
    }

    @Override
    public boolean tryPaint(int subHit, int unused, int paintColor) {
        if (subHit == 29) {
            if (this.paintColor == paintColor) {
                return false;
            }
            this.paintColor = (byte) paintColor;
            this.updateBlockChange();
            return true;
        }
        return false;
    }

    @Override
    public boolean tubeItemCanEnter(int arg0, int arg1, TubeItem arg2) {
        return (arg2.color == 0 || this.paintColor == 0 || arg2.color == this.paintColor) && arg1 == 0;
    }

    @Override
    public boolean tubeItemEnter(int arg0, int arg1, TubeItem arg2) {
        if (arg1 != 0) {
            return false;
        }
        if (arg2.color != 0 && this.paintColor != 0 && arg2.color != this.paintColor) {
            return false;
        }
        arg2.side = (byte) arg0;
        this.flow.add(arg2);
        this.hasChanged = true;
        this.dirtyBlock();
        return true;
    }

    @Override
    public int tubeWeight(int arg0, int arg1) {
        return 0;
    }

    @Override
    protected void writeToPacket(Packet211TileDesc packet) {
        super.writeToPacket(packet);
        packet.addByte(this.paintColor);
    }

}
