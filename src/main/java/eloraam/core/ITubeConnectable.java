/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.TubeItem;

public interface ITubeConnectable {

    public int getTubeConnectableSides();

    public int getTubeConClass();

    public boolean canRouteItems();

    public boolean tubeItemEnter(int var1, int var2, TubeItem var3);

    public boolean tubeItemCanEnter(int var1, int var2, TubeItem var3);

    public int tubeWeight(int var1, int var2);
}
