/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.RedPowerLib;
import eloraam.core.WorldCoord;
import java.util.HashSet;
import java.util.LinkedList;

public abstract class WirePathfinder {

    HashSet scanmap;
    LinkedList scanpos;

    public void init() {
	this.scanmap = new HashSet();
	this.scanpos = new LinkedList();
    }

    public void addSearchBlock(WorldCoord worldCoord) {
	if (this.scanmap.contains(worldCoord)) {
	    return;
	}
	this.scanmap.add(worldCoord);
	this.scanpos.addLast(worldCoord);
    }

    private void addIndBl(WorldCoord worldCoord, int n, int n2) {
	int n3;
	worldCoord = worldCoord.coordStep(n);
	switch (n) {
	case 0: {
	    n3 = n2 + 2;
	    break;
	}
	case 1: {
	    n3 = n2 + 2;
	    break;
	}
	case 2: {
	    n3 = n2 + (n2 & 2);
	    break;
	}
	case 3: {
	    n3 = n2 + (n2 & 2);
	    break;
	}
	case 4: {
	    n3 = n2;
	    break;
	}
	default: {
	    n3 = n2;
	}
	}
	worldCoord.step(n3);
	this.addSearchBlock(worldCoord);
    }

    public void addSearchBlocks(WorldCoord worldCoord, int n, int n2) {
	int n3;
	for (n3 = 0; n3 < 6; ++n3) {
	    if ((n & RedPowerLib.getConDirMask(n3)) <= 0)
		continue;
	    this.addSearchBlock(worldCoord.coordStep(n3));
	}
	for (n3 = 0; n3 < 6; ++n3) {
	    for (int i = 0; i < 4; ++i) {
		if ((n2 & 1 << n3 * 4 + i) <= 0)
		    continue;
		this.addIndBl(worldCoord, n3, i);
	    }
	}
    }

    public boolean step(WorldCoord worldCoord) {
	return false;
    }

    public boolean iterate() {
	if (this.scanpos.size() == 0) {
	    return false;
	}
	WorldCoord worldCoord = (WorldCoord) this.scanpos.removeFirst();
	return this.step(worldCoord);
    }
}
