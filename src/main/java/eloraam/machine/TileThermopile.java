/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.BlockFire
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 *  net.minecraft.server.WorldProvider
 */
package eloraam.machine;

import eloraam.core.*;
import net.minecraft.server.*;

public class TileThermopile
        extends TileExtended
        implements IBluePowerConnectable {
    BluePowerConductor cond;
    public int tempHot;
    public int tempCold;
    public int ticks;
    public int ConMask;

    public TileThermopile() {
        this.cond = new BluePowerConductor() {

            @Override
            public TileEntity getParent() {
                return TileThermopile.this;
            }

            @Override
            public double getInvCap() {
                return 4.0;
            }
        };
        this.tempHot = 0;
        this.tempCold = 0;
        this.ticks = 0;
        this.ConMask = -1;
    }

    @Override
    public int getConnectableMask() {
        return 1073741823;
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
    public int getExtendedID() {
        return 11;
    }

    @Override
    public int getBlockID() {
        return RedPowerMachine.blockMachine.id;
    }

    private void updateTemps() {
        WorldCoord worldCoord;
        int n;
        int n2;
        int n3 = 0;
        int n4 = 0;
        for (n = 0; n < 6; ++n) {
            worldCoord = new WorldCoord(this);
            worldCoord.step(n);
            n2 = this.world.getTypeId(worldCoord.x, worldCoord.y, worldCoord.z);
            if (this.world.isEmpty(worldCoord.x, worldCoord.y, worldCoord.z)) {
                if (this.world.worldProvider.d) {
                    ++n3;
                    continue;
                }
                ++n4;
                continue;
            }
            if (n2 == Block.SNOW_BLOCK.id) {
                n4 += 100;
                continue;
            }
            if (n2 == Block.ICE.id) {
                n4 += 100;
                continue;
            }
            if (n2 == Block.SNOW.id) {
                n4 += 50;
                continue;
            }
            if (n2 == Block.TORCH.id) {
                n3 += 5;
                continue;
            }
            if (n2 == Block.JACK_O_LANTERN.id) {
                n3 += 3;
                continue;
            }
            if (n2 == Block.WATER.id || n2 == Block.STATIONARY_WATER.id) {
                n4 += 25;
                continue;
            }
            if (n2 == Block.LAVA.id || n2 == Block.STATIONARY_LAVA.id) {
                n3 += 100;
                continue;
            }
            if (n2 != Block.FIRE.id) continue;
            n3 += 25;
        }
        if (this.tempHot >= 100 && this.tempCold >= 200) {
            for (n = 0; n < 6; ++n) {
                worldCoord = new WorldCoord(this);
                worldCoord.step(n);
                n2 = this.world.getTypeId(worldCoord.x, worldCoord.y, worldCoord.z);
                if (n2 != Block.LAVA.id && n2 != Block.STATIONARY_LAVA.id || this.world.random.nextInt(300) != 0)
                    continue;
                int n5 = this.world.getData(worldCoord.x, worldCoord.y, worldCoord.z);
                this.world.setTypeIdAndData(worldCoord.x, worldCoord.y, worldCoord.z, n5 != 0 ? RedPowerWorld.blockStone.id : Block.OBSIDIAN.id, n5 <= 0 ? 0 : 1);
                break;
            }
        }
        if (this.tempHot >= 100) {
            for (n = 0; n < 6; ++n) {
                if (this.world.random.nextInt(300) != 0) continue;
                worldCoord = new WorldCoord(this);
                worldCoord.step(n);
                n2 = this.world.getTypeId(worldCoord.x, worldCoord.y, worldCoord.z);
                if (n2 == Block.SNOW.id) {
                    this.world.setTypeId(worldCoord.x, worldCoord.y, worldCoord.z, 0);
                    break;
                }
                if (n2 != Block.ICE.id && n2 != Block.SNOW_BLOCK.id) continue;
                this.world.setTypeId(worldCoord.x, worldCoord.y, worldCoord.z, this.world.worldProvider.d ? 0 : Block.WATER.id);
                break;
            }
        }
        this.tempHot = n3;
        this.tempCold = n4;
    }

    @Override
    public void q_() {
        super.q_();
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (this.ConMask < 0) {
            this.ConMask = RedPowerLib.getConnections((IBlockAccess) this.world, this, this.x, this.y, this.z);
            this.cond.recache(this.ConMask, 0);
        }
        this.cond.iterate();
        this.dirtyBlock();
        if (this.cond.getVoltage() > 100.0) {
            return;
        }
        ++this.ticks;
        if (this.ticks > 20) {
            this.ticks = 0;
            this.updateTemps();
        }
        int n = Math.min(this.tempHot, this.tempCold);
        this.cond.applyDirect(0.005 * (double) n);
    }

    @Override
    public void onBlockNeighborChange(int n) {
        this.ConMask = -1;
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        this.cond.readFromNBT(nBTTagCompound);
        this.tempHot = nBTTagCompound.getShort("hot");
        this.tempCold = nBTTagCompound.getShort("cold");
        this.ticks = nBTTagCompound.getByte("ticks");
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        this.cond.writeToNBT(nBTTagCompound);
        nBTTagCompound.setShort("hot", (short) this.tempHot);
        nBTTagCompound.setShort("cold", (short) this.tempCold);
        nBTTagCompound.setByte("ticks", (byte) this.ticks);
    }

}

