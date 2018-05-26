/* X-RP - decompiled with CFR */
package eloraam.core;

import java.io.IOException;
import net.minecraft.server.IBlockAccess;

public interface IFrameSupport {

    public byte[] getFramePacket();

    public void handleFramePacket(byte[] var1) throws IOException;

    public void onFrameRefresh(IBlockAccess var1);

    public void onFramePickup(IBlockAccess var1);

    public void onFrameDrop();
}
