/* X-RP - decompiled with CFR */
package eloraam.core;

public interface IFrameLink {

    public boolean isFrameMoving();

    public boolean canFrameConnectIn(int var1);

    public boolean canFrameConnectOut(int var1);

    public WorldCoord getFrameLinkset();
}
