/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.IBlockAccess;

import java.io.IOException;

public interface IFrameSupport {

    public byte[] getFramePacket();

    public void handleFramePacket(byte[] var1) throws IOException;

    public void onFrameRefresh(IBlockAccess var1);

    public void onFramePickup(IBlockAccess var1);

    public void onFrameDrop();
}
