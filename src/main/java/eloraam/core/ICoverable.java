/* X-RP - decompiled with CFR */
package eloraam.core;

public interface ICoverable {

    public boolean canAddCover(int var1, int var2);

    public boolean tryAddCover(int var1, int var2);

    public int tryRemoveCover(int var1);

    public int getCover(int var1);

    public int getCoverMask();
}
