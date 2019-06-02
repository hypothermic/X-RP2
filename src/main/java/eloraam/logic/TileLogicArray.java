/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.World
 */
package eloraam.logic;

import eloraam.core.*;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;

import java.io.IOException;

public class TileLogicArray
        extends TileLogic
        implements IRedPowerWiring {
    public short PowerVal1 = 0;
    public short PowerVal2 = 0;

    @Override
    public int getPoweringMask(int n) {
        if (n != 0) {
            return 0;
        }
        int n2 = 0;
        if (this.PowerVal1 > 0) {
            n2 |= RedPowerLib.mapRotToCon(10, this.Rotation);
        }
        if (this.PowerVal2 > 0) {
            n2 |= RedPowerLib.mapRotToCon(5, this.Rotation);
        }
        return n2;
    }

    @Override
    public void updateCurrentStrength() {
        this.PowerVal2 = (short) RedPowerLib.updateBlockCurrentStrength(this.world, this, this.x, this.y, this.z, RedPowerLib.mapRotToCon(5, this.Rotation), 1);
        this.PowerVal1 = (short) RedPowerLib.updateBlockCurrentStrength(this.world, this, this.x, this.y, this.z, RedPowerLib.mapRotToCon(10, this.Rotation), 1);
        CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
    }

    @Override
    public int getCurrentStrength(int n, int n2) {
        if (n2 != 0) {
            return -1;
        }
        if ((RedPowerLib.mapRotToCon(5, this.Rotation) & n) > 0) {
            return this.PowerVal2;
        }
        if ((RedPowerLib.mapRotToCon(10, this.Rotation) & n) > 0) {
            return this.PowerVal1;
        }
        return -1;
    }

    @Override
    public int scanPoweringStrength(int n, int n2) {
        if (n2 != 0) {
            return 0;
        }
        int n3 = RedPowerLib.mapRotToCon(5, this.Rotation);
        int n4 = RedPowerLib.mapRotToCon(10, this.Rotation);
        if ((n3 & n) > 0) {
            if (this.Powered) {
                return 255;
            }
            return !RedPowerLib.isPowered((IBlockAccess) this.world, this.x, this.y, this.z, n3 & n, 0) ? 0 : 255;
        }
        if ((n4 & n) > 0) {
            return !RedPowerLib.isPowered((IBlockAccess) this.world, this.x, this.y, this.z, n4 & n, 0) ? 0 : 255;
        }
        return 0;
    }

    @Override
    public int getConnectionMask() {
        return RedPowerLib.mapRotToCon(15, this.Rotation);
    }

    @Override
    public int getExtConnectionMask() {
        return 0;
    }

    public int getTopwireMask() {
        return RedPowerLib.mapRotToCon(5, this.Rotation);
    }

    private boolean cellWantsPower() {
        if (this.SubId == 1) {
            return this.PowerState == 0;
        }
        return this.PowerState != 0;
    }

    private void updatePowerState() {
        int n = this.PowerState = this.PowerVal1 <= 0 ? 0 : 1;
        if (this.cellWantsPower() != this.Powered) {
            this.scheduleTick(2);
        }
    }

    @Override
    public int getExtendedID() {
        return 2;
    }

    @Override
    public void onBlockNeighborChange(int n) {
        if (this.tryDropBlock()) {
            return;
        }
        RedPowerLib.updateCurrent(this.world, this.x, this.y, this.z);
        if (this.SubId == 0) {
            return;
        }
        if (this.isTickRunnable()) {
            return;
        }
        this.updatePowerState();
    }

    @Override
    public boolean isBlockStrongPoweringTo(int n) {
        if (RedPowerLib.isSearching()) {
            return false;
        }
        return (this.getPoweringMask(0) & RedPowerLib.getConDirMask(n ^ 1)) > 0;
    }

    @Override
    public boolean isBlockWeakPoweringTo(int n) {
        if (RedPowerLib.isSearching()) {
            return false;
        }
        return (this.getPoweringMask(0) & RedPowerLib.getConDirMask(n ^ 1)) > 0;
    }

    @Override
    public void onTileTick() {
        if (this.Powered != this.cellWantsPower()) {
            this.Powered = !this.Powered;
            this.updateBlockChange();
            this.updatePowerState();
        }
    }

    @Override
    public void setPartBounds(BlockMultipart blockMultipart, int n) {
        if (n != this.Rotation >> 2) {
            super.setPartBounds(blockMultipart, n);
            return;
        }
        switch (n) {
            case 0: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, 1.0f, 0.75f, 1.0f);
                break;
            }
            case 1: {
                blockMultipart.a(0.0f, 0.15f, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 2: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.75f);
                break;
            }
            case 3: {
                blockMultipart.a(0.0f, 0.0f, 0.15f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 4: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, 0.75f, 1.0f, 1.0f);
                break;
            }
            case 5: {
                blockMultipart.a(0.15f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        this.PowerVal1 = (short) (nBTTagCompound.getByte("pv1") & 255);
        this.PowerVal2 = (short) (nBTTagCompound.getByte("pv2") & 255);
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        nBTTagCompound.setByte("pv1", (byte) this.PowerVal1);
        nBTTagCompound.setByte("pv2", (byte) this.PowerVal2);
    }

    @Override
    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        super.readFromPacket(packet211TileDesc);
        if (packet211TileDesc.subId != 6) {
            return;
        }
        this.PowerVal1 = (short) packet211TileDesc.getByte();
        this.PowerVal2 = (short) packet211TileDesc.getByte();
    }

    @Override
    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        super.writeToPacket(packet211TileDesc);
        packet211TileDesc.subId = 6;
        packet211TileDesc.addByte(this.PowerVal1);
        packet211TileDesc.addByte(this.PowerVal2);
    }
}

