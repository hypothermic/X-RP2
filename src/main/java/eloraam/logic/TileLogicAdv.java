/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 */
package eloraam.logic;

import eloraam.core.IRedPowerWiring;
import eloraam.core.Packet211TileDesc;
import eloraam.core.RedPowerLib;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;

import java.io.IOException;

public class TileLogicAdv
        extends TileLogic
        implements IRedPowerWiring {
    LogicAdvModule storage = null;

    @Override
    public void updateCurrentStrength() {
        this.initStorage();
        this.storage.updateCurrentStrength();
    }

    @Override
    public int getCurrentStrength(int n, int n2) {
        this.initStorage();
        return (this.storage.getPoweringMask(n2) & n) <= 0 ? -1 : 255;
    }

    @Override
    public int scanPoweringStrength(int n, int n2) {
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

    @Override
    public int getConnectClass(int n) {
        int n2 = RedPowerLib.mapRotToCon(10, this.Rotation);
        return (n2 & RedPowerLib.getConDirMask(n)) <= 0 ? 0 : 18;
    }

    @Override
    public int getExtendedID() {
        return 4;
    }

    @Override
    public void initSubType(int n) {
        this.SubId = n;
        this.initStorage();
    }

    public LogicAdvModule getLogicStorage(Class class_) {
        if (!class_.isInstance(this.storage)) {
            this.initStorage();
        }
        return this.storage;
    }

    public boolean isUseableByPlayer(EntityHuman entityHuman) {
        if (this.world.getTileEntity(this.x, this.y, this.z) != this) {
            return false;
        }
        return entityHuman.e((double) this.x + 0.5, (double) this.y + 0.5, (double) this.z + 0.5) <= 64.0;
    }

    @Override
    public int getPartMaxRotation(int n, boolean bl) {
        if (bl) {
            switch (this.SubId) {
                case 0: {
                    return 1;
                }
            }
        }
        return super.getPartMaxRotation(n, bl);
    }

    @Override
    public int getPartRotation(int n, boolean bl) {
        if (bl) {
            switch (this.SubId) {
                case 0: {
                    return this.Deadmap;
                }
            }
        }
        return super.getPartRotation(n, bl);
    }

    @Override
    public void setPartRotation(int n, boolean bl, int n2) {
        if (bl) {
            switch (this.SubId) {
                case 0: {
                    this.Deadmap = n2;
                    this.updateBlockChange();
                    return;
                }
            }
        }
        super.setPartRotation(n, bl, n2);
    }

    void initStorage() {
        if (this.storage != null && this.storage.getSubType() == this.SubId) {
            return;
        }
        switch (this.SubId) {
            case 0: {
                this.storage = new LogicAdvXcvr();
                break;
            }
            default: {
                this.storage = null;
            }
        }
    }

    @Override
    public void onBlockNeighborChange(int n) {
        if (this.tryDropBlock()) {
            return;
        }
        this.initStorage();
        switch (this.SubId) {
            case 0: {
                if (this.isTickRunnable()) {
                    return;
                }
                this.storage.updatePowerState();
            }
        }
    }

    @Override
    public void onTileTick() {
        this.initStorage();
        this.storage.tileTick();
    }

    @Override
    public int getPoweringMask(int n) {
        this.initStorage();
        return this.storage.getPoweringMask(n);
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        this.initStorage();
        this.storage.readFromNBT(nBTTagCompound);
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        this.storage.writeToNBT(nBTTagCompound);
    }

    @Override
    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        super.readFromPacket(packet211TileDesc);
        this.initStorage();
        this.storage.readFromPacket(packet211TileDesc);
    }

    @Override
    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        super.writeToPacket(packet211TileDesc);
        this.storage.writeToPacket(packet211TileDesc);
    }

    public abstract class LogicAdvModule {
        public abstract void updatePowerState();

        public abstract void tileTick();

        public abstract int getSubType();

        public abstract int getPoweringMask(int var1);

        public void updateCurrentStrength() {
        }

        public abstract void readFromNBT(NBTTagCompound var1);

        public abstract void writeToNBT(NBTTagCompound var1);

        public void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        }

        public void writeToPacket(Packet211TileDesc packet211TileDesc) {
        }
    }

    public class LogicAdvXcvr
            extends LogicAdvModule {
        public int State1;
        public int State2;
        public int State1N;
        public int State2N;

        @Override
        public void updatePowerState() {
            int n = RedPowerLib.getRotPowerState((IBlockAccess) TileLogicAdv.this.world, TileLogicAdv.this.x, TileLogicAdv.this.y, TileLogicAdv.this.z, 5, TileLogicAdv.this.Rotation, 0);
            if (n != TileLogicAdv.this.PowerState) {
                TileLogicAdv.this.PowerState = n;
                TileLogicAdv.this.updateBlock();
                TileLogicAdv.this.scheduleTick(2);
            }
        }

        @Override
        public void tileTick() {
            TileLogicAdv.this.Powered = (TileLogicAdv.this.PowerState & 1) > 0;
            TileLogicAdv.this.Active = (TileLogicAdv.this.PowerState & 4) > 0;
            int n = this.State1N;
            int n2 = this.State2N;
            if (TileLogicAdv.this.Deadmap == 0) {
                if (!TileLogicAdv.this.Powered) {
                    n = 0;
                }
                if (!TileLogicAdv.this.Active) {
                    n2 = 0;
                }
            } else {
                if (!TileLogicAdv.this.Powered) {
                    n2 = 0;
                }
                if (!TileLogicAdv.this.Active) {
                    n = 0;
                }
            }
            boolean bl = this.State1 != n || this.State2 != n2;
            this.State1 = n;
            this.State2 = n2;
            if (bl) {
                TileLogicAdv.this.updateBlock();
                RedPowerLib.updateCurrent(TileLogicAdv.this.world, TileLogicAdv.this.x, TileLogicAdv.this.y, TileLogicAdv.this.z);
            }
            this.updatePowerState();
            this.updateCurrentStrength();
        }

        @Override
        public int getSubType() {
            return 0;
        }

        @Override
        public int getPoweringMask(int n) {
            int n2 = 0;
            if (n < 1 || n > 16) {
                return 0;
            }
            if ((this.State1 >> --n & 1) > 0) {
                n2 |= 8;
            }
            if ((this.State2 >> n & 1) > 0) {
                n2 |= 2;
            }
            return RedPowerLib.mapRotToCon(n2, TileLogicAdv.this.Rotation);
        }

        @Override
        public void updateCurrentStrength() {
            if (TileLogicAdv.this.isTickRunnable()) {
                return;
            }
            this.State1N = this.State2;
            this.State2N = this.State1;
            for (int i = 0; i < 16; ++i) {
                short s = (short) RedPowerLib.updateBlockCurrentStrength(TileLogicAdv.this.world, TileLogicAdv.this, TileLogicAdv.this.x, TileLogicAdv.this.y, TileLogicAdv.this.z, RedPowerLib.mapRotToCon(2, TileLogicAdv.this.Rotation), 2 << i);
                short s2 = (short) RedPowerLib.updateBlockCurrentStrength(TileLogicAdv.this.world, TileLogicAdv.this, TileLogicAdv.this.x, TileLogicAdv.this.y, TileLogicAdv.this.z, RedPowerLib.mapRotToCon(8, TileLogicAdv.this.Rotation), 2 << i);
                if (s > 0) {
                    this.State1N |= 1 << i;
                }
                if (s2 <= 0) continue;
                this.State2N |= 1 << i;
            }
            TileLogicAdv.this.dirtyBlock();
            if (this.State1N != this.State1 || this.State2N != this.State2) {
                TileLogicAdv.this.scheduleTick(2);
            }
        }

        @Override
        public void readFromNBT(NBTTagCompound nBTTagCompound) {
            this.State1 = nBTTagCompound.getInt("s1");
            this.State2 = nBTTagCompound.getInt("s2");
            this.State1N = nBTTagCompound.getInt("s1n");
            this.State2N = nBTTagCompound.getInt("s2n");
        }

        @Override
        public void writeToNBT(NBTTagCompound nBTTagCompound) {
            nBTTagCompound.setInt("s1", this.State1);
            nBTTagCompound.setInt("s2", this.State2);
            nBTTagCompound.setInt("s1n", this.State1N);
            nBTTagCompound.setInt("s2n", this.State2N);
        }

        @Override
        public void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
            this.State1 = (int) packet211TileDesc.getUVLC();
            this.State2 = (int) packet211TileDesc.getUVLC();
        }

        @Override
        public void writeToPacket(Packet211TileDesc packet211TileDesc) {
            packet211TileDesc.addUVLC(this.State1);
            packet211TileDesc.addUVLC(this.State2);
        }

        public LogicAdvXcvr() {
            this.State1 = 0;
            this.State2 = 0;
            this.State1N = 0;
            this.State2N = 0;
        }
    }

}

