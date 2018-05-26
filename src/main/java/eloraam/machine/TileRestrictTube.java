/* X-RP - decompiled with CFR */
package eloraam.machine;

import eloraam.machine.TileTube;

public class TileRestrictTube
extends TileTube {
    @Override
    public int tubeWeight(int n, int n2) {
        return 1000000;
    }

    @Override
    public int getExtendedID() {
        return 10;
    }
}

