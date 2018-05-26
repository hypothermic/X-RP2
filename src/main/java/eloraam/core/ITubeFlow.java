/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.ITubeConnectable;
import eloraam.core.TubeFlow;
import eloraam.core.TubeItem;

public interface ITubeFlow extends ITubeConnectable {

    public void addTubeItem(TubeItem var1);

    public TubeFlow getTubeFlow();
}
