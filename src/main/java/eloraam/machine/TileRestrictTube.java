/* X-RP - decompiled with CFR */
package eloraam.machine;

public class TileRestrictTube extends TileTube {

    public static final int RESTRICTION_TUBE_WEIGHT = 1000000;

    @Override
    public int tubeWeight(int n, int n2) {
        return RESTRICTION_TUBE_WEIGHT;
    }

    @Override
    public int getExtendedID() {
        return 10;
    }
}

