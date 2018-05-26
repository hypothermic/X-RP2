/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 *  net.minecraft.server.WorldProvider
 */
package eloraam.machine;

import eloraam.core.BlockMultipart;
import eloraam.core.BluePowerConductor;
import eloraam.core.CoreProxy;
import eloraam.core.IBluePowerConnectable;
import eloraam.core.RedPowerLib;
import eloraam.machine.TileMachinePanel;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import net.minecraft.server.WorldProvider;

public class TileSolarPanel
extends TileMachinePanel
implements IBluePowerConnectable {
    BluePowerConductor cond;
    public int ConMask;

    public TileSolarPanel() {
        this.cond = new BluePowerConductor(){

            @Override
            public TileEntity getParent() {
                return TileSolarPanel.this;
            }

            @Override
            public double getInvCap() {
                return 4.0;
            }
        };
        this.ConMask = -1;
    }

    @Override
    public void onBlockNeighborChange(int n) {
        this.ConMask = -1;
        if (this.world.isBlockSolidOnSide(this.x, this.y - 1, this.z, 1)) {
            return;
        }
        this.breakBlock();
    }

    @Override
    public void setPartBounds(BlockMultipart blockMultipart, int n) {
        blockMultipart.a(0.0f, 0.0f, 0.0f, 1.0f, 0.25f, 1.0f);
    }

    @Override
    public int getConnectableMask() {
        return 16777231;
    }

    @Override
    public int getConnectClass(int n) {
        return 64;
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
    public void q_() {
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (this.ConMask < 0) {
            this.ConMask = RedPowerLib.getConnections((IBlockAccess)this.world, this, this.x, this.y, this.z);
            this.cond.recache(this.ConMask, 0);
        }
        this.cond.iterate();
        this.dirtyBlock();
        if (this.cond.getVoltage() > 100.0) {
            return;
        }
        if (!this.world.isChunkLoaded(this.x, this.y, this.z)) {
            return;
        }
        if (!this.world.e()) {
            return;
        }
        if (this.world.worldProvider.e) {
            return;
        }
        this.cond.applyDirect(2.0);
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        this.cond.readFromNBT(nBTTagCompound);
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        this.cond.writeToNBT(nBTTagCompound);
    }

}

