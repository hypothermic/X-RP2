/* X-RP - decompiled with CFR */
package eloraam.machine;

import eloraam.core.BlockMultipart;

public class TileMagTube
        extends TileTube {
    @Override
    public int getTubeConnectableSides() {
        int n = 63;
        for (int i = 0; i < 6; ++i) {
            if ((this.CoverSides & 1 << i) <= 0 || this.Covers[i] >> 8 >= 3) continue;
            n &= ~(1 << i);
        }
        return n;
    }

    public int getSpeed() {
        return 128;
    }

    @Override
    public int getTubeConClass() {
        return 18 + this.paintColor;
    }

    @Override
    public void setPartBounds(BlockMultipart blockMultipart, int n) {
        if (n == 29) {
            blockMultipart.a(0.125f, 0.125f, 0.125f, 0.875f, 0.875f, 0.875f);
        } else {
            super.setPartBounds(blockMultipart, n);
        }
    }

    @Override
    public int getExtendedID() {
        return 11;
    }
}

