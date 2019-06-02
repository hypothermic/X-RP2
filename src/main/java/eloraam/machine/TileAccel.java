/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.EntityLiving
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.*;
import net.minecraft.server.*;

import java.io.IOException;
import java.util.Iterator;

public class TileAccel
        extends TileMachinePanel
        implements IBluePowerConnectable,
        ITubeFlow {
    TubeFlow flow;
    BluePowerEndpoint cond;
    private boolean hasChanged;
    public int ConMask;
    public int conCache;

    public TileAccel() {
        this.flow = new TubeFlow() {

            @Override
            public TileEntity getParent() {
                return TileAccel.this;
            }

            @Override
            public boolean schedule(TubeItem arg0, TubeFlow.TubeScheduleContext arg1) {
                arg0.scheduled = true;
                arg0.progress = 0;
                arg0.side = (byte) (arg0.side ^ 1);
                TileAccel.this.recache();
                arg0.power = 0;
                if ((arg0.side == TileAccel.this.Rotation && (TileAccel.this.conCache & 2) > 0 || arg0.side == (TileAccel.this.Rotation ^ 1) && (TileAccel.this.conCache & 8) > 0) && TileAccel.this.cond.getVoltage() >= 60.0) {
                    TileAccel.this.cond.drawPower(100 * arg0.item.count);
                    arg0.power = 255;
                }
                return true;
            }
        };
        this.cond = new BluePowerEndpoint() {

            @Override
            public TileEntity getParent() {
                return TileAccel.this;
            }
        };
        this.hasChanged = false;
        this.ConMask = -1;
        this.conCache = -1;
    }

    @Override
    public void a(NBTTagCompound arg0) {
        super.a(arg0);
        this.cond.readFromNBT(arg0);
        this.flow.readFromNBT(arg0);
    }

    @Override
    public void addTubeItem(TubeItem arg0) {
        arg0.side = (byte) (arg0.side ^ 1);
        this.flow.add(arg0);
        this.hasChanged = true;
        this.dirtyBlock();
    }

    @Override
    public void b(NBTTagCompound arg0) {
        super.b(arg0);
        this.cond.writeToNBT(arg0);
        this.flow.writeToNBT(arg0);
    }

    @Override
    public boolean canRouteItems() {
        return true;
    }

    @Override
    public BluePowerConductor getBlueConductor() {
        return this.cond;
    }

    @Override
    public int getConnectableMask() {
        return 1073741823;
    }

    @Override
    public int getConnectClass(int arg0) {
        return 65;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public int getExtendedID() {
        return 2;
    }

    @Override
    public int getLightValue() {
        return this.Charged ? 6 : 0;
    }

    @Override
    public int getPartMaxRotation(int arg0, boolean arg1) {
        return !arg1 ? 5 : 0;
    }

    @Override
    public int getTubeConClass() {
        return 17;
    }

    @Override
    public int getTubeConnectableSides() {
        return 3 << (this.Rotation & 6);
    }

    @Override
    public TubeFlow getTubeFlow() {
        return this.flow;
    }

    @Override
    public void onBlockNeighborChange(int arg0) {
        this.ConMask = -1;
        this.conCache = -1;
    }

    @Override
    public void onBlockPlacedBy(EntityLiving arg0) {
        this.Rotation = this.getFacing(arg0);
    }

    @Override
    public void onHarvestPart(EntityHuman arg0, int arg1) {
        this.flow.onRemove();
        this.breakBlock();
    }

    @Override
    public void q_() {
        super.q_();
        if (this.flow.update()) {
            this.hasChanged = true;
        }
        if (this.hasChanged) {
            this.hasChanged = false;
            if (CoreProxy.isServer()) {
                this.sendItemUpdate();
            }
            this.dirtyBlock();
        }
        if (!CoreProxy.isClient(this.world)) {
            if (this.ConMask < 0) {
                this.ConMask = RedPowerLib.getConnections((IBlockAccess) this.world, this, this.x, this.y, this.z);
                this.cond.recache(this.ConMask, 0);
            }
            this.cond.iterate();
            this.dirtyBlock();
            if (this.cond.Flow == 0) {
                if (this.Charged) {
                    this.Charged = false;
                    this.updateBlock();
                    this.updateLight();
                }
            } else if (!this.Charged) {
                this.Charged = true;
                this.updateBlock();
                this.updateLight();
            }
        }
    }

    @Override
    protected void readFromPacket(Packet211TileDesc arg0) throws IOException {
        if (arg0.subId == 10) {
            this.flow.contents.clear();
            int arg1 = (int) arg0.getUVLC();
            int arg2 = 0;
            while (arg2 < arg1) {
                this.flow.contents.add(TubeItem.newFromPacket(arg0));
                ++arg2;
            }
        } else {
            super.readFromPacket(arg0);
            this.updateBlock();
        }
    }

    public void recache() {
        if (this.conCache < 0) {
            int arg3;
            WorldCoord arg0 = new WorldCoord(this);
            ITubeConnectable arg1 = (ITubeConnectable) CoreLib.getTileEntity((IBlockAccess) this.world, arg0.coordStep(this.Rotation), ITubeConnectable.class);
            ITubeConnectable arg2 = (ITubeConnectable) CoreLib.getTileEntity((IBlockAccess) this.world, arg0.coordStep(this.Rotation ^ 1), ITubeConnectable.class);
            this.conCache = 0;
            if (arg1 != null) {
                arg3 = arg1.getTubeConClass();
                if (arg3 < 17) {
                    this.conCache |= 1;
                } else if (arg3 >= 17) {
                    this.conCache |= 2;
                }
            }
            if (arg2 != null) {
                arg3 = arg2.getTubeConClass();
                if (arg3 < 17) {
                    this.conCache |= 4;
                } else if (arg3 >= 17) {
                    this.conCache |= 8;
                }
            }
        }
    }

    protected void sendItemUpdate() {
        Packet211TileDesc arg0 = new Packet211TileDesc();
        arg0.subId = 10;
        arg0.xCoord = this.x;
        arg0.yCoord = this.y;
        arg0.zCoord = this.z;
        int arg1 = this.flow.contents.size();
        if (arg1 > 6) {
            arg1 = 6;
        }
        arg0.addUVLC(arg1);
        Iterator arg2 = this.flow.contents.iterator();
        int arg3 = 0;
        while (arg3 < arg1) {
            TubeItem arg4 = (TubeItem) arg2.next();
            arg4.writeToPacket(arg0);
            ++arg3;
        }
        arg0.encode();
        CoreProxy.sendPacketToPosition(arg0, this.x, this.z);
    }

    @Override
    public boolean tubeItemCanEnter(int arg0, int arg1, TubeItem arg2) {
        if (arg1 == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean tubeItemEnter(int arg0, int arg1, TubeItem arg2) {
        if (arg1 != 0) {
            return false;
        }
        if (arg0 != this.Rotation && arg0 != (this.Rotation ^ 1)) {
            return false;
        }
        arg2.side = (byte) arg0;
        this.flow.add(arg2);
        this.hasChanged = true;
        this.dirtyBlock();
        return true;
    }

    @Override
    public int tubeWeight(int arg0, int arg1) {
        return 0;
    }

}

