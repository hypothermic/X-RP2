/* X-RP - decompiled with CFR */
package eloraam.machine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import dreadend.RP2.mutithread.RP2ThreadManager;
//import dreadend.RP2.mutithread.ScheduledTubeRunnable;
import eloraam.core.BlockMultipart;
import eloraam.core.CoreLib;
import eloraam.core.CoreProxy;
import eloraam.core.IPaintable;
import eloraam.core.ITubeFlow;
import eloraam.core.MachineLib;
import eloraam.core.Packet211TileDesc;
import eloraam.core.TileCovered;
import eloraam.core.TubeFlow;
import eloraam.core.TubeItem;
import eloraam.core.TubeLib;
import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.RedPowerBase;
import net.minecraft.server.RedPowerMachine;
import net.minecraft.server.TileEntity;

public class TileTube extends TileCovered implements ITubeFlow, IPaintable {

    TubeFlow flow;
    public static volatile byte lastDir; // X-RP2: static volatile
    public byte paintColor;
    private boolean hasChanged;
    final TileTube ThIs;
    public static LinkedBlockingQueue<TileTube> tileTubeQueue = new LinkedBlockingQueue();

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
				continue;
			    }
			    Thread.sleep(50);
			} while (true);
		    } catch (Exception e) {
			e.printStackTrace();
			continue;
		    }
		} while (true);
	    }
	}.start();
    }

    public TileTube() {
        this.flow = new TubeFlow(){

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
                int arg2 = arg1.cons & ~ (1 << arg0.side);
                if (arg2 == 0) {
                    return true;
                }
                if (Integer.bitCount(arg2) == 1) {
                    arg0.side = (byte)Integer.numberOfTrailingZeros(arg2);
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
        this.ThIs = this;
    }

    @Override
    public void a(NBTTagCompound arg0) {
	super.a(arg0);
	this.flow.readFromNBT(arg0);
	this.lastDir = arg0.getByte("lDir");
	this.paintColor = arg0.getByte("pCol");
    }

    @Override
    public void addHarvestContents(ArrayList arg0) {
	super.addHarvestContents(arg0);
	arg0.add(new ItemStack(RedPowerBase.blockMicro.id, 1, this.getExtendedID() << 8));
    }

    @Override
    public void addTubeItem(TubeItem arg0) {
	arg0.side = (byte) (arg0.side ^ 1);
	this.flow.add(arg0);
	this.hasChanged = true;
	this.dirtyBlock();
    }

    @Override
    public void b(NBTTagCompound arg0) {
	super.b(arg0);
	this.flow.writeToNBT(arg0);
	arg0.setByte("lDir", this.lastDir);
	arg0.setByte("pCol", this.paintColor);
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

    @Override
    public float getPartStrength(EntityHuman arg0, int arg1) {
	BlockMachine arg2 = RedPowerMachine.blockMachine;
	return arg1 == 29 ? arg0.getCurrentPlayerStrVsBlock((Block) arg2, 0) / (arg2.m() * 30.0f) : super.getPartStrength(arg0, arg1);
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
    protected void readFromPacket(Packet211TileDesc arg0) throws IOException {
	if (arg0.subId == 10) {
	    this.flow.contents.clear();
	    int arg1 = (int) arg0.getUVLC();
	    int arg2 = 0;
	    while (arg2 < arg1) {
		this.flow.contents.add(TubeItem.newFromPacket(arg0));
		++arg2;
	    }
	} else {
	    super.readFromPacket(arg0);
	    this.paintColor = (byte) arg0.getByte();
	}
    }

    protected void sendItemUpdate() {
	Packet211TileDesc arg0 = new Packet211TileDesc();
	arg0.subId = 10;
	arg0.xCoord = this.x;
	arg0.yCoord = this.y;
	arg0.zCoord = this.z;
	int arg1 = this.flow.contents.size();
	if (arg1 > 6) {
	    arg1 = 6;
	}
	arg0.addUVLC(arg1);
	try {
	    int maxmin = Math.min(5, this.flow.contents.size());
	    int arg3 = 0;
	    while (arg3 < maxmin) {
		TubeItem arg4 = (TubeItem) this.flow.contents.get(arg3);
		arg4.writeToPacket(arg0);
		++arg3;
	    }
	    arg0.encode();
	    CoreProxy.sendPacketToPosition(arg0, this.x, this.z);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void setPartBounds(BlockMultipart arg0, int arg1) {
	if (arg1 == 29) {
	    arg0.a(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
	} else {
	    super.setPartBounds(arg0, arg1);
	}
    }

    @Override
    public boolean tryPaint(int arg0, int arg1, int arg2) {
	if (arg0 == 29) {
	    if (this.paintColor == arg2) {
		return false;
	    }
	    this.paintColor = (byte) arg2;
	    this.updateBlockChange();
	    return true;
	}
	return false;
    }

    @Override
    public boolean tubeItemCanEnter(int arg0, int arg1, TubeItem arg2) {
	return arg2.color != 0 && this.paintColor != 0 && arg2.color != this.paintColor ? false : arg1 == 0;
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
    protected void writeToPacket(Packet211TileDesc arg0) {
	super.writeToPacket(arg0);
	arg0.addByte(this.paintColor);
    }

}
