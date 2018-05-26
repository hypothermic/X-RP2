/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.CoreLib;
import eloraam.core.ITubeConnectable;
import eloraam.core.MachineLib;
import eloraam.core.TubeItem;
import eloraam.core.WorldCoord;
import forge.ISidedInventory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public class TubeLib {

    private static HashSet tubeClassMapping = new HashSet();

    static {
	TubeLib.addCompatibleMapping(0, 17);
	TubeLib.addCompatibleMapping(17, 18);
	int arg = 0;
	while (arg < 16) {
	    TubeLib.addCompatibleMapping(0, 1 + arg);
	    TubeLib.addCompatibleMapping(17, 1 + arg);
	    TubeLib.addCompatibleMapping(17, 19 + arg);
	    TubeLib.addCompatibleMapping(18, 19 + arg);
	    ++arg;
	}
    }

    public static void addCompatibleMapping(int arg, int arg0) {
	tubeClassMapping.add(Arrays.asList(arg, arg0));
	tubeClassMapping.add(Arrays.asList(arg0, arg));
    }

    public static boolean addToTubeRoute(World arg, ItemStack arg0, WorldCoord arg1, WorldCoord arg2, int arg3) {
	return TubeLib.addToTubeRoute(arg, new TubeItem(0, arg0), arg1, arg2, arg3);
    }

    public static boolean addToTubeRoute(World arg, TubeItem arg0, WorldCoord arg1, WorldCoord arg2, int arg3) {
	ITubeConnectable arg4 = (ITubeConnectable) CoreLib.getTileEntity((IBlockAccess) arg, arg2, ITubeConnectable.class);
	if (arg4 == null) {
	    return false;
	}
	arg0.mode = 1;
	int arg5 = TubeLib.findRoute(arg, arg1, arg0, 1 << (arg3 ^ 1), 1);
	return arg5 < 0 ? false : arg4.tubeItemEnter(arg3, 0, arg0);
    }

    public static int findRoute(World arg, WorldCoord arg0, TubeItem arg1, int arg2, int arg3) {
	OutRouteFinder arg4 = new OutRouteFinder(arg, arg1, arg3);
	return arg4.find(arg0, arg2);
    }

    public static int findRoute(World arg, WorldCoord arg0, TubeItem arg1, int arg2, int arg3, int arg4) {
	OutRouteFinder arg5 = new OutRouteFinder(arg, arg1, arg3);
	arg5.startDir = arg4;
	return arg5.find(arg0, arg2);
    }

    public static int getConnections(IBlockAccess arg, int arg0, int arg1, int arg2) {
	ITubeConnectable arg3 = (ITubeConnectable) CoreLib.getTileEntity(arg, arg0, arg1, arg2, ITubeConnectable.class);
	if (arg3 == null) {
	    return 0;
	}
	int arg4 = 0;
	int arg5 = arg3.getTubeConClass();
	int arg6 = arg3.getTubeConnectableSides();
	if ((arg6 & 1) > 0 && TubeLib.isConSide(arg, arg0, arg1 - 1, arg2, arg5, 1)) {
	    arg4 |= 1;
	}
	if ((arg6 & 2) > 0 && TubeLib.isConSide(arg, arg0, arg1 + 1, arg2, arg5, 0)) {
	    arg4 |= 2;
	}
	if ((arg6 & 4) > 0 && TubeLib.isConSide(arg, arg0, arg1, arg2 - 1, arg5, 3)) {
	    arg4 |= 4;
	}
	if ((arg6 & 8) > 0 && TubeLib.isConSide(arg, arg0, arg1, arg2 + 1, arg5, 2)) {
	    arg4 |= 8;
	}
	if ((arg6 & 16) > 0 && TubeLib.isConSide(arg, arg0 - 1, arg1, arg2, arg5, 5)) {
	    arg4 |= 16;
	}
	if ((arg6 & 32) > 0 && TubeLib.isConSide(arg, arg0 + 1, arg1, arg2, arg5, 4)) {
	    arg4 |= 32;
	}
	return arg4;
    }

    public static boolean isCompatible(int arg, int arg0) {
	if (arg != arg0 && !tubeClassMapping.contains(Arrays.asList(arg, arg0))) {
	    return false;
	}
	return true;
    }

    private static boolean isConSide(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3, int arg4) {
	TileEntity arg5 = arg.getTileEntity(arg0, arg1, arg2);
	if (TubeLib.isCompatible(arg3, 0) && arg5 instanceof IInventory) {
	    if (!(arg5 instanceof ISidedInventory)) {
		return true;
	    }
	    ISidedInventory arg6 = (ISidedInventory) arg5;
	    if (arg6.getSizeInventorySide(arg4) > 0) {
		return true;
	    }
	}
	if (arg5 instanceof ITubeConnectable) {
	    ITubeConnectable arg8 = (ITubeConnectable) arg5;
	    if (!TubeLib.isCompatible(arg3, arg8.getTubeConClass())) {
		return false;
	    }
	    int arg7 = arg8.getTubeConnectableSides();
	    if ((arg7 & 1 << arg4) > 0) {
		return true;
	    }
	    return false;
	}
	return false;
    }

    public static class InRouteFinder extends RouteFinder {

	MachineLib.FilterMap filterMap;
	int subFilt = -1;

	public InRouteFinder(World arg0, MachineLib.FilterMap arg1) {
	    super(arg0);
	    this.filterMap = arg1;
	}

	@Override
	public void addPoint(WorldCoord arg0, int arg1, int arg2, int arg3) {
	    IInventory arg4 = MachineLib.getInventory(this.worldObj, arg0);
	    if (arg4 == null) {
		super.addPoint(arg0, arg1, arg2, arg3);
	    } else {
		int arg5 = (arg2 ^ 1) & 63;
		int arg6 = 0;
		int arg7 = arg4.getSize();
		if (arg4 instanceof ISidedInventory) {
		    ISidedInventory arg8 = (ISidedInventory) arg4;
		    arg6 = arg8.getStartInventorySide(arg5);
		    arg7 = arg8.getSizeInventorySide(arg5);
		}
		if (this.filterMap.size() == 0) {
		    if (!MachineLib.emptyInventory(arg4, arg6, arg7)) {
			WorldRoute arg11 = new WorldRoute(arg0, 0, arg5, arg3);
			arg11.solved = true;
			this.scanpos.add(arg11);
		    } else {
			super.addPoint(arg0, arg1, arg2, arg3);
		    }
		} else {
		    int arg10 = -1;
		    if (this.subFilt < 0) {
			arg10 = MachineLib.matchAnyStack(this.filterMap, arg4, arg6, arg7);
		    } else if (MachineLib.matchOneStack(this.filterMap, arg4, arg6, arg7, this.subFilt)) {
			arg10 = this.subFilt;
		    }
		    if (arg10 < 0) {
			super.addPoint(arg0, arg1, arg2, arg3);
		    } else {
			WorldRoute arg9 = new WorldRoute(arg0, arg10, arg5, arg3);
			arg9.solved = true;
			this.scanpos.add(arg9);
		    }
		}
	    }
	}

	@Override
	public int find(WorldCoord arg0, int arg1) {
	    return super.find(arg0, arg1);
	}

	public WorldCoord getResultPoint() {
	    return this.result.wc;
	}

	public int getResultSide() {
	    return this.result.side;
	}

	public void setSubFilt(int arg0) {
	    this.subFilt = arg0;
	}
    }

    private static class OutRouteFinder extends RouteFinder {

	int state;
	TubeItem tubeItem;

	public OutRouteFinder(World arg0, TubeItem arg1, int arg2) {
	    super(arg0);
	    this.state = arg2;
	    this.tubeItem = arg1;
	}

	@Override
	public void addPoint(WorldCoord arg0, int arg1, int arg2, int arg3) {
	    int arg4 = (arg2 ^ 1) & 255;
	    if (this.state != 3 && MachineLib.canAddToInventory(this.worldObj, this.tubeItem.item, arg0, arg4)) {
		WorldRoute arg7 = new WorldRoute(arg0, arg1, arg2, arg3);
		arg7.solved = true;
		this.scanpos.add(arg7);
	    } else {
		ITubeConnectable arg5 = (ITubeConnectable) CoreLib.getTileEntity((IBlockAccess) this.worldObj, arg0, ITubeConnectable.class);
		if (arg5 != null) {
		    if (arg5.tubeItemCanEnter(arg4, this.state, this.tubeItem)) {
			WorldRoute arg6 = new WorldRoute(arg0, arg1, arg4, arg3 + arg5.tubeWeight(arg4, this.state));
			arg6.solved = true;
			this.scanpos.add(arg6);
		    } else if (arg5.tubeItemCanEnter(arg4, 0, this.tubeItem) && arg5.canRouteItems() && !this.scanmap.contains(arg0)) {
			this.scanmap.add(arg0);
			this.scanpos.add(new WorldRoute(arg0, arg1, arg4, arg3 + arg5.tubeWeight(arg4, this.state)));
		    }
		}
	    }
	}
    }

    private static class RouteFinder {

	int startDir = 0;
	WorldRoute result;
	World worldObj;
	HashSet scanmap = new HashSet();
	PriorityQueue scanpos = new PriorityQueue();

	public RouteFinder(World arg0) {
	    this.worldObj = arg0;
	}

	public void addPoint(WorldCoord arg0, int arg1, int arg2, int arg3) {
	    ITubeConnectable arg4 = (ITubeConnectable) CoreLib.getTileEntity((IBlockAccess) this.worldObj, arg0, ITubeConnectable.class);
	    if (arg4 != null && arg4.canRouteItems() && !this.scanmap.contains(arg0)) {
		this.scanmap.add(arg0);
		this.scanpos.add(new WorldRoute(arg0, arg1, arg2 ^ 1, arg3));
	    }
	}

	public int find(WorldCoord arg0, int arg1) {
	    int arg2 = 0;
	    while (arg2 < 6) {
		if ((arg1 & 1 << arg2) != 0) {
		    WorldCoord arg3 = arg0.copy();
		    arg3.step(arg2);
		    this.addPoint(arg3, arg2, arg2, arg2 != this.startDir ? 1 : 0);
		}
		++arg2;
	    }
	    while (this.scanpos.size() > 0) {
		WorldRoute arg6 = (WorldRoute) this.scanpos.poll();
		if (arg6.solved) {
		    this.result = arg6;
		    return arg6.start;
		}
		int arg7 = TubeLib.getConnections((IBlockAccess) this.worldObj, arg6.wc.x, arg6.wc.y, arg6.wc.z);
		int arg4 = 0;
		while (arg4 < 6) {
		    if (arg4 != arg6.side && (arg7 & 1 << arg4) != 0) {
			WorldCoord arg5 = arg6.wc.copy();
			arg5.step(arg4);
			this.addPoint(arg5, arg6.start, arg4, arg6.weight + 2);
		    }
		    ++arg4;
		}
	    }
	    return -1;
	}
    }

    private static class WorldRoute implements Comparable {

	public WorldCoord wc;
	public int start;
	public int side;
	public int weight;
	public boolean solved = false;

	public WorldRoute(WorldCoord arg0, int arg1, int arg2, int arg3) {
	    this.wc = arg0;
	    this.start = arg1;
	    this.side = arg2;
	    this.weight = arg3;
	}

	public int compareTo(Object arg0) {
	    WorldRoute arg1 = (WorldRoute) arg0;
	    return this.weight - arg1.weight;
	}
    }

}
