/* X-RP - decompiled with CFR */
package eloraam.wiring;

import eloraam.base.BlockMicro;
import eloraam.core.*;
import net.minecraft.server.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class TileWiring extends TileCovered implements IWiring {

    public int ConSides = 0;
    public int Metadata = 0;
    public short CenterPost = 0;
    public int ConMask = -1;
    public int EConMask = -1;
    public int ConaMask = -1;

    private static int stripBlockConMask(int arg) {
        switch (arg) {
            case 0: {
                return 257;
            }
            case 1: {
                return 4098;
            }
            case 2: {
                return 65540;
            }
            case 3: {
                return 1048584;
            }
            case 4: {
                return 263168;
            }
            case 5: {
                return 540672;
            }
            case 6: {
                return 4196352;
            }
            case 7: {
                return 8421376;
            }
            case 8: {
                return 528;
            }
            case 9: {
                return 8224;
            }
            case 10: {
                return 131136;
            }
        }
        return 2097280;
    }

    @Override
    public void a(NBTTagCompound arg0) {
        super.a(arg0);
        this.ConSides = arg0.getByte("cons") & 255;
        this.Metadata = arg0.getByte("md") & 255;
        this.CenterPost = (short) (arg0.getShort("post") & 255);
    }

    @Override
    public void addHarvestContents(ArrayList arg0) {
        super.addHarvestContents(arg0);
        int arg1 = 0;
        while (arg1 < 6) {
            if ((this.ConSides & 1 << arg1) != 0) {
                arg0.add(new ItemStack(RedPowerBase.blockMicro.id, 1, this.getExtendedID() * 256 + this.Metadata));
            }
            ++arg1;
        }
        if ((this.ConSides & 64) > 0) {
            arg1 = 16384 + this.CenterPost;
            if (this.getExtendedID() == 3) {
                arg1 += 256;
            }
            if (this.getExtendedID() == 5) {
                arg1 += 512;
            }
            arg0.add(new ItemStack(RedPowerBase.blockMicro.id, 1, arg1));
        }
    }

    @Override
    public void b(NBTTagCompound arg0) {
        super.b(arg0);
        arg0.setByte("cons", (byte) this.ConSides);
        arg0.setByte("md", (byte) this.Metadata);
        arg0.setShort("post", this.CenterPost);
    }

    @Override
    public boolean blockEmpty() {
        if (this.CoverSides == 0 && this.ConSides == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canAddCover(int arg0, int arg1) {
        if (arg0 < 6 && (this.ConSides & 1 << arg0) > 0) {
            return false;
        }
        if ((this.CoverSides & 1 << arg0) > 0) {
            return false;
        }
        short[] arg2 = Arrays.copyOf(this.Covers, 29);
        arg2[arg0] = (short) arg1;
        return CoverLib.checkPlacement(this.CoverSides | 1 << arg0, arg2, this.ConSides, (this.ConSides & 64) > 0);
    }

    @Override
    public int getConnectableMask() {
        if (this.ConaMask >= 0) {
            return this.ConaMask;
        }
        int arg0 = 0;
        if ((this.ConSides & 1) > 0) {
            arg0 |= 15;
        }
        if ((this.ConSides & 2) > 0) {
            arg0 |= 240;
        }
        if ((this.ConSides & 4) > 0) {
            arg0 |= 3840;
        }
        if ((this.ConSides & 8) > 0) {
            arg0 |= 61440;
        }
        if ((this.ConSides & 16) > 0) {
            arg0 |= 983040;
        }
        if ((this.ConSides & 32) > 0) {
            arg0 |= 15728640;
        }
        if ((this.CoverSides & 1) > 0) {
            arg0 &= -1118465;
        }
        if ((this.CoverSides & 2) > 0) {
            arg0 &= -2236929;
        }
        if ((this.CoverSides & 4) > 0) {
            arg0 &= -4456466;
        }
        if ((this.CoverSides & 8) > 0) {
            arg0 &= -8912931;
        }
        if ((this.CoverSides & 16) > 0) {
            arg0 &= -17477;
        }
        if ((this.CoverSides & 32) > 0) {
            arg0 &= -34953;
        }
        int arg1 = 0;
        while (arg1 < 12) {
            if ((this.CoverSides & 16384 << arg1) > 0) {
                arg0 &= ~TileWiring.stripBlockConMask(arg1);
            }
            ++arg1;
        }
        if ((this.ConSides & 64) > 0) {
            arg0 |= 1056964608;
            arg1 = 0;
            while (arg1 < 6) {
                if ((this.CoverSides & 1 << arg1) > 0) {
                    int arg2 = this.Covers[arg1] >> 8;
                    if (arg2 < 3) {
                        arg0 &= ~(1 << arg1 + 24);
                    }
                    if (arg2 == 5) {
                        arg0 &= 3 << (arg1 & -2) + 24;
                    }
                }
                ++arg1;
            }
        }
        this.ConaMask = arg0;
        return arg0;
    }

    @Override
    public int getConnectionMask() {
        if (this.ConMask >= 0) {
            return this.ConMask;
        }
        this.ConMask = RedPowerLib.getConnections((IBlockAccess) this.world, this, this.x, this.y, this.z);
        return this.ConMask;
    }

    @Override
    public int getCornerPowerMode() {
        return 1;
    }

    @Override
    public int getExtConnectionMask() {
        if (this.EConMask >= 0) {
            return this.EConMask;
        }
        this.EConMask = RedPowerLib.getExtConnections((IBlockAccess) this.world, this, this.x, this.y, this.z);
        return this.EConMask;
    }

    @Override
    public int getExtendedMetadata() {
        return this.Metadata;
    }

    @Override
    public int getPartsMask() {
        return this.CoverSides | this.ConSides & 63 | (this.ConSides & 64) << 23;
    }

    @Override
    public float getPartStrength(EntityHuman arg0, int arg1) {
        BlockMicro arg2 = RedPowerBase.blockMicro;
        return arg1 == 29 && (this.ConSides & 64) > 0 ? arg0.getCurrentPlayerStrVsBlock((Block) arg2, 0) / (arg2.m() * 30.0f) : ((this.ConSides & 1 << arg1) > 0 ? arg0.getCurrentPlayerStrVsBlock((Block) arg2, 0) / (arg2.m() * 30.0f) : super.getPartStrength(arg0, arg1));
    }

    @Override
    public int getSolidPartsMask() {
        return this.CoverSides | (this.ConSides & 64) << 23;
    }

    public float getWireHeight() {
        return 0.125f;
    }

    @Override
    public void onBlockNeighborChange(int arg0) {
        if (this.EConMask >= 0 || this.ConMask >= 0) {
            this.world.notify(this.x, this.y, this.z);
        }
        this.ConMask = -1;
        this.EConMask = -1;
        this.refreshBlockSupport();
        RedPowerLib.updateCurrent(this.world, this.x, this.y, this.z);
    }

    @Override
    public void onFrameRefresh(IBlockAccess arg0) {
        if (this.ConMask < 0) {
            this.ConMask = RedPowerLib.getConnections(arg0, this, this.x, this.y, this.z);
        }
        if (this.EConMask < 0) {
            this.EConMask = RedPowerLib.getExtConnections(arg0, this, this.x, this.y, this.z);
        }
    }

    @Override
    public void onHarvestPart(EntityHuman arg0, int arg1) {
        boolean arg2 = false;
        if (arg1 == 29 && (this.ConSides & 64) > 0) {
            int arg3 = 16384 + this.CenterPost;
            if (this.getExtendedID() == 3) {
                arg3 += 256;
            }
            if (this.getExtendedID() == 5) {
                arg3 += 512;
            }
            CoreLib.dropItem(this.world, this.x, this.y, this.z, new ItemStack(RedPowerBase.blockMicro.id, 1, arg3));
            this.ConSides &= 63;
        } else {
            if ((this.ConSides & 1 << arg1) <= 0) {
                super.onHarvestPart(arg0, arg1);
                return;
            }
            CoreLib.dropItem(this.world, this.x, this.y, this.z, new ItemStack(RedPowerBase.blockMicro.id, 1, this.getExtendedID() * 256 + this.Metadata));
            this.ConSides &= ~(1 << arg1);
        }
        this.uncache();
        if (this.ConSides == 0) {
            if (this.CoverSides > 0) {
                this.replaceWithCovers();
            } else {
                this.deleteBlock();
            }
        }
        CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
        RedPowerLib.updateIndirectNeighbors(this.world, this.x, this.y, this.z, RedPowerBase.blockMicro.id);
    }

    @Override
    protected void readFromPacket(Packet211TileDesc arg0) throws IOException {
        super.readFromPacket(arg0);
        this.Metadata = arg0.getByte();
        this.ConSides = arg0.getByte();
        if ((this.ConSides & 64) > 0) {
            this.CenterPost = (short) arg0.getUVLC();
        }
        this.ConaMask = -1;
        this.EConMask = -1;
        this.ConMask = -1;
    }

    public boolean refreshBlockSupport() {
        boolean arg0 = false;
        int arg1 = this.ConSides & 63;
        if (arg1 == 3 || arg1 == 12 || arg1 == 48) {
            arg0 = true;
        }
        int arg2 = 0;
        while (arg2 < 6) {
            if ((this.ConSides & 1 << arg2) != 0 && (arg0 || !RedPowerLib.canSupportWire((IBlockAccess) this.world, this.x, this.y, this.z, arg2))) {
                this.uncache();
                CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
                CoreLib.dropItem(this.world, this.x, this.y, this.z, new ItemStack(RedPowerBase.blockMicro.id, 1, this.getExtendedID() * 256 + this.Metadata));
                this.ConSides &= ~(1 << arg2);
            }
            ++arg2;
        }
        if (this.ConSides == 0) {
            if (this.CoverSides > 0) {
                this.replaceWithCovers();
            } else {
                this.deleteBlock();
            }
            return false;
        }
        return true;
    }

    @Override
    public void setExtendedMetadata(int arg0) {
        this.Metadata = arg0;
    }

    @Override
    public void setPartBounds(BlockMultipart arg0, int arg1) {
        if (arg1 == 29) {
            if ((this.ConSides & 64) == 0) {
                super.setPartBounds(arg0, arg1);
                return;
            }
        } else if ((this.ConSides & 1 << arg1) == 0) {
            super.setPartBounds(arg0, arg1);
            return;
        }
        float arg2 = this.getWireHeight();
        switch (arg1) {
            case 0: {
                arg0.a(0.0f, 0.0f, 0.0f, 1.0f, arg2, 1.0f);
                break;
            }
            case 1: {
                arg0.a(0.0f, 1.0f - arg2, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 2: {
                arg0.a(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, arg2);
                break;
            }
            case 3: {
                arg0.a(0.0f, 0.0f, 1.0f - arg2, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 4: {
                arg0.a(0.0f, 0.0f, 0.0f, arg2, 1.0f, 1.0f);
                break;
            }
            case 5: {
                arg0.a(1.0f - arg2, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 29: {
                arg0.a(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
            }
        }
    }

    @Override
    public boolean tryAddCover(int arg0, int arg1) {
        if (!this.canAddCover(arg0, arg1)) {
            return false;
        }
        this.CoverSides |= 1 << arg0;
        this.Covers[arg0] = (short) arg1;
        this.uncache();
        this.updateBlockChange();
        return true;
    }

    @Override
    public int tryRemoveCover(int arg0) {
        int arg1 = super.tryRemoveCover(arg0);
        if (arg1 < 0) {
            return -1;
        }
        this.uncache();
        this.updateBlockChange();
        return arg1;
    }

    public void uncache() {
        if (this.ConaMask >= 0 || this.EConMask >= 0 || this.ConMask >= 0) {
            this.world.notify(this.x, this.y, this.z);
        }
        this.ConaMask = -1;
        this.EConMask = -1;
        this.ConMask = -1;
    }

    public void uncache0() {
        this.EConMask = -1;
        this.ConMask = -1;
    }

    @Override
    protected void writeToPacket(Packet211TileDesc arg0) {
        super.writeToPacket(arg0);
        arg0.addByte(this.Metadata);
        arg0.addByte(this.ConSides);
        if ((this.ConSides & 64) > 0) {
            arg0.addUVLC(this.CenterPost);
        }
    }
}
