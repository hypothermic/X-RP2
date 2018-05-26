/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.EntityLiving
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.BluePowerConductor;
import eloraam.core.BluePowerEndpoint;
import eloraam.core.CoreLib;
import eloraam.core.CoreProxy;
import eloraam.core.FluidBuffer;
import eloraam.core.IBluePowerConnectable;
import eloraam.core.IPipeConnectable;
import eloraam.core.PipeLib;
import eloraam.core.RedPowerLib;
import eloraam.core.WorldCoord;
import eloraam.machine.TileMachinePanel;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public class TilePump
extends TileMachinePanel
implements IPipeConnectable,
IBluePowerConnectable {
    PumpBuffer inbuf;
    PumpBuffer outbuf;
    BluePowerEndpoint cond;
    public int ConMask;
    public byte PumpTick;

    public TilePump() {
        this.inbuf = new PumpBuffer();
        this.outbuf = new PumpBuffer();
        this.cond = new BluePowerEndpoint(){

            @Override
            public TileEntity getParent() {
                return TilePump.this;
            }
        };
        this.ConMask = -1;
        this.PumpTick = 0;
    }

    @Override
    public int getPipeConnectableSides() {
        return 12 << (((this.Rotation ^ 1) & 1) << 1);
    }

    @Override
    public int getPipeFlangeSides() {
        return 12 << (((this.Rotation ^ 1) & 1) << 1);
    }

    @Override
    public int getPipePressure(int n) {
        int n2 = CoreLib.rotToSide(this.Rotation);
        if (!this.Active) {
            return 0;
        }
        if (n == n2) {
            return 1000;
        }
        return n != ((n2 ^ 1) & 255) ? 0 : -1000;
    }

    @Override
    public FluidBuffer getPipeBuffer(int n) {
        int n2 = CoreLib.rotToSide(this.Rotation);
        if (n == n2) {
            return this.outbuf;
        }
        if (n == ((n2 ^ 1) & 255)) {
            return this.inbuf;
        }
        return null;
    }

    @Override
    public int getConnectableMask() {
        return 3 << ((this.Rotation & 1) << 1);
    }

    @Override
    public int getConnectClass(int n) {
        return 65;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public BluePowerConductor getBlueConductor() {
        return this.cond;
    }

    @Override
    public int getExtendedID() {
        return 1;
    }

    @Override
    public void onBlockPlacedBy(EntityLiving entityLiving) {
        this.Rotation = (int)Math.floor((double)(entityLiving.yaw * 4.0f / 360.0f) + 2.5) & 3;
    }

    @Override
    public void onBlockNeighborChange(int n) {
        this.ConMask = -1;
        if (RedPowerLib.isPowered((IBlockAccess)this.world, this.x, this.y, this.z, 16777215, 63)) {
            if (this.Powered) {
                return;
            }
        } else {
            this.Powered = false;
            this.dirtyBlock();
            return;
        }
        this.Powered = true;
        this.dirtyBlock();
    }

    private void pumpFluid() {
        if (this.inbuf.Type == 0) {
            return;
        }
        int n = Math.min(this.inbuf.getLevel(), this.outbuf.getMaxLevel() - this.outbuf.getLevel());
        if ((n = Math.min(n, this.inbuf.getLevel() + this.inbuf.Delta)) <= 0) {
            return;
        }
        if (this.inbuf.Type != this.outbuf.Type && this.outbuf.Type != 0) {
            return;
        }
        this.outbuf.addLevel(this.inbuf.Type, n);
        this.inbuf.addLevel(this.inbuf.Type, - n);
    }

    @Override
    public void q_() {
        super.q_();
        if (CoreProxy.isClient(this.world)) {
            if (this.Active) {
                this.PumpTick = (byte)(this.PumpTick + 1);
                if (this.PumpTick >= 16) {
                    this.PumpTick = 0;
                }
            }
            return;
        }
        if (this.ConMask < 0) {
            this.ConMask = RedPowerLib.getConnections((IBlockAccess)this.world, this, this.x, this.y, this.z);
            this.cond.recache(this.ConMask, 0);
        }
        this.cond.iterate();
        this.dirtyBlock();
        int n = CoreLib.rotToSide(this.Rotation);
        PipeLib.movePipeLiquid(this.world, this, new WorldCoord(this), 3 << (n & -2));
        boolean bl = this.Active;
        if (this.Active) {
            this.PumpTick = (byte)(this.PumpTick + 1);
            if (this.PumpTick == 8) {
                this.cond.drawPower(10000.0);
                this.pumpFluid();
            }
            if (this.PumpTick >= 16) {
                this.PumpTick = 0;
                this.Active = false;
            }
            this.cond.drawPower(200.0);
        }
        if (this.cond.getVoltage() < 60.0) {
            if (this.Charged && this.cond.Flow == 0) {
                this.Charged = false;
                this.updateBlock();
            }
            return;
        }
        if (!this.Charged) {
            this.Charged = true;
            this.updateBlock();
        }
        if (this.Charged && this.Powered) {
            this.Active = true;
        }
        if (this.Active != bl) {
            this.updateBlock();
        }
    }

    @Override
    public void onTileTick() {
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (!this.Powered) {
            this.Active = false;
            this.updateBlock();
        }
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        this.cond.readFromNBT(nBTTagCompound);
        this.inbuf.readFromNBT(nBTTagCompound, "inb");
        this.outbuf.readFromNBT(nBTTagCompound, "outb");
        this.PumpTick = nBTTagCompound.getByte("ptk");
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        this.cond.writeToNBT(nBTTagCompound);
        this.inbuf.writeToNBT(nBTTagCompound, "inb");
        this.outbuf.writeToNBT(nBTTagCompound, "outb");
        nBTTagCompound.setByte("ptk", this.PumpTick);
    }

    private class PumpBuffer
    extends FluidBuffer {
        @Override
        public TileEntity getParent() {
            return TilePump.this;
        }

        @Override
        public void onChange() {
            TilePump.this.dirtyBlock();
        }

        @Override
        public int getMaxLevel() {
            return 32;
        }
    }

}

