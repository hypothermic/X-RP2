package dreadend.RP2.mutithread;

/*import eloraam.core.TubeFlow;
import eloraam.core.TubeItem;
import eloraam.core.TubeLib;
import eloraam.machine.TileTube;

public class ScheduledTubeRunnable implements Runnable {
    
    public TubeItem tube;
    public TubeFlow.TubeScheduleContext context;
    public int side;

    public ScheduledTubeRunnable(TubeItem arg0, TubeFlow.TubeScheduleContext arg1, int arg2) {
	tube = arg0;
	context = arg1;
	side = arg2;
    }

    public void run() {
	tube.side = ((byte) TubeLib.findRoute(context.world, context.wc, tube, side, tube.mode, TileTube.lastDir));

	if (tube.side >= 0) {
	    int arg3 = side & ((2 << TileTube.lastDir) - 1 ^ 0xFFFFFFFF);
	    if (arg3 == 0) {
		arg3 = side;
	    }

	    if (arg3 == 0) {
		TileTube.lastDir = 0;
	    } else {
		TileTube.lastDir = ((byte) Integer.numberOfTrailingZeros(arg3));
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
		tube.side = TileTube.lastDir;
		int arg3 = side & ((2 << TileTube.lastDir) - 1 ^ 0xFFFFFFFF);
		if (arg3 == 0) {
		    arg3 = side;
		}

		if (arg3 == 0) {
		    TileTube.lastDir = 0;
		} else {
		    TileTube.lastDir = ((byte) Integer.numberOfTrailingZeros(arg3));
		}
	    }
	}
    }
}*/