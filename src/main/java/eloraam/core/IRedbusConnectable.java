/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.IConnectable;

public interface IRedbusConnectable extends IConnectable {

    public int rbGetAddr();

    public void rbSetAddr(int var1);

    public int rbRead(int var1);

    public void rbWrite(int var1, int var2);
}
