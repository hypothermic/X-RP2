/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.BlockFire
 *  net.minecraft.server.BlockPortal
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.RedPowerLib;
import eloraam.core.WorldCoord;
import eloraam.machine.TileMachine;
import net.minecraft.server.Block;
import net.minecraft.server.BlockFire;
import net.minecraft.server.BlockPortal;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public class TileIgniter
extends TileMachine {
    @Override
    public int getExtendedID() {
        return 12;
    }

    private void fireAction() {
        WorldCoord worldCoord = new WorldCoord(this);
        worldCoord.step(this.Rotation ^ 1);
        if (this.Active) {
            if (this.world.isEmpty(worldCoord.x, worldCoord.y, worldCoord.z)) {
                this.world.setTypeId(worldCoord.x, worldCoord.y, worldCoord.z, Block.FIRE.id);
            }
        } else {
            int n = this.world.getTypeId(worldCoord.x, worldCoord.y, worldCoord.z);
            if (n == Block.FIRE.id || n == Block.PORTAL.id) {
                this.world.setTypeId(worldCoord.x, worldCoord.y, worldCoord.z, 0);
            }
        }
    }

    @Override
    public void onBlockNeighborChange(int n) {
        if (!RedPowerLib.isPowered((IBlockAccess)this.world, this.x, this.y, this.z, 16777215, 63)) {
            if (!this.Powered) {
                return;
            }
            this.Powered = false;
            if (this.Delay) {
                return;
            }
            this.Active = false;
            this.Delay = true;
            this.fireAction();
        } else {
            if (this.Powered) {
                return;
            }
            this.Powered = true;
            if (this.Delay) {
                return;
            }
            if (this.Active) {
                return;
            }
            this.Active = true;
            this.Delay = true;
            this.fireAction();
        }
        this.scheduleTick(5);
        this.updateBlock();
    }

    public boolean isOnFire(int n) {
        if (this.Rotation != 0) {
            return false;
        }
        return this.Active;
    }

    @Override
    public void onTileTick() {
        this.Delay = false;
        if (this.Active == this.Powered) {
            return;
        }
        this.Active = this.Powered;
        this.fireAction();
        this.updateBlock();
    }
}

