/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.wiring.TileWiring;

public class TileRibbon extends TileWiring {

    @Override
    public int getExtendedID() {
        return 12;
    }

    @Override
    public int getConnectClass(int n) {
        return 66;
    }

    public int getCurrentStrength(int n, int n2) {
        return 0;
    }

    public int scanPoweringStrength(int n, int n2) {
        return 0;
    }

    public void updateCurrentStrength() {
    }

    public int getPoweringMask(int n) {
        return 0;
    }
}
