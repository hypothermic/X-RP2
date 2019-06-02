/* X-RP - decompiled with CFR */
package eloraam.logic;

import eloraam.core.*;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.RedPowerLogic;

import java.io.IOException;

public class TileLogicPointer extends TileLogic implements IIntervalSet, IPointerTile {

    private long timestart = 0;
    private long intervalk;
    private long minInterval = 26;

    public TileLogicPointer() {
        this.SetInterval(this.minInterval);
    }

    @Override
    public void initSubType(int paramInt) {
        super.initSubType(paramInt);
        switch (paramInt) {
            case 0: {
                this.SetInterval(this.minInterval);
                break;
            }
            case 2: {
                this.Disabled = true;
            }
        }
    }

    @Override
    public int getPartMaxRotation(int paramInt, boolean paramBoolean) {
        if (paramBoolean && (this.SubId == 1 || this.SubId == 2)) {
            return 1;
        }
        return super.getPartMaxRotation(paramInt, paramBoolean);
    }

    @Override
    public int getPartRotation(int paramInt, boolean paramBoolean) {
        if (paramBoolean && (this.SubId == 1 || this.SubId == 2)) {
            return this.Deadmap;
        }
        return super.getPartRotation(paramInt, paramBoolean);
    }

    @Override
    public void setPartRotation(int paramInt1, boolean paramBoolean, int paramInt2) {
        if (paramBoolean && (this.SubId == 1 || this.SubId == 2)) {
            this.Deadmap = paramInt2;
            this.updateBlockChange();
            return;
        }
        super.setPartRotation(paramInt1, paramBoolean, paramInt2);
    }

    private void timerChange() {
        int i = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 7, this.Rotation, 0);
        if (i != this.PowerState) {
            this.updateBlock();
        }
        this.PowerState = i;
        if (this.Powered) {
            if (!this.Disabled) {
                return;
            }
            if (i > 0) {
                return;
            }
            this.Powered = false;
            this.Disabled = false;
            this.timestart = this.world.getTime();
            this.updateBlock();
        } else if (this.Disabled) {
            if (i > 0) {
                return;
            }
            this.timestart = this.world.getTime();
            this.Disabled = false;
            this.updateBlock();
        } else {
            if (i == 0) {
                return;
            }
            this.Disabled = true;
            this.updateBlock();
        }
    }

    private void timerTick() {
        int i = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 7, this.Rotation, 0);
        if (i != this.PowerState) {
            this.updateBlock();
        }
        this.PowerState = i;
        if (this.Powered) {
            if (this.Disabled) {
                if (i > 0) {
                    this.Powered = false;
                    this.updateBlock();
                    return;
                }
                this.Disabled = false;
                this.Powered = false;
                this.timestart = this.world.getTime();
                this.updateBlock();
                return;
            }
            if (i == 0) {
                this.Powered = false;
            } else {
                this.Disabled = true;
                this.scheduleTick(2);
            }
            this.timestart = this.world.getTime();
            this.updateBlockChange();
        } else if (this.Disabled) {
            if (i > 0) {
                return;
            }
            this.timestart = this.world.getTime();
            this.Disabled = false;
            this.updateBlock();
        } else {
            if (i == 0) {
                return;
            }
            this.Disabled = true;
            this.updateBlock();
        }
    }

    private void timerUpdate() {
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (this.Powered || this.Disabled) {
            return;
        }
        long l = this.world.getTime();
        if (this.GetInterval() < this.minInterval) {
            this.SetInterval(this.minInterval);
        }
        if (this.timestart > l) {
            this.timestart = l;
        }
        if (this.timestart + this.GetInterval() <= l) {
            this.playSound("random.click", 0.3f, 0.5f, false);
            this.Powered = true;
            this.scheduleTick(2);
            this.updateBlockChange();
        }
    }

    private void sequencerUpdate() {
        long l = this.world.getTime() + 6000;
        float f = (float) l / (float) (this.GetInterval() * 4);
        int i = (int) Math.floor(f * 4.0f);
        i = this.Deadmap == 1 ? 3 - i & 3 : i + 3 & 3;
        if (this.PowerState != i && !CoreProxy.isClient(this.world)) {
            this.playSound("random.click", 0.3f, 0.5f, false);
            this.PowerState = i;
            this.updateBlockChange();
        }
    }

    private void stateCellChange() {
        int i = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 7, this.Rotation, 0);
        if (i != this.PowerState) {
            this.updateBlock();
        }
        this.PowerState = i;
        int j = (i & 0x3) > 0 ? 1 : Deadmap != 0 ? 0 : (i & 0x6) > 0 ? 1 : 0;
        if (this.Disabled && j == 0) {
            this.Disabled = false;
            this.timestart = this.world.getTime();
            this.updateBlock();
        } else if (!this.Disabled && j != 0) {
            this.Disabled = true;
            this.updateBlock();
        }
        if (!this.Active && !this.Powered && (i & 2) > 0) {
            this.Powered = true;
            this.updateBlock();
            this.scheduleTick(2);
        }
    }

    private void stateCellTick() {
        if (!this.Active && this.Powered) {
            this.Powered = false;
            this.Active = true;
            this.timestart = this.world.getTime();
            this.updateBlockChange();
        } else if (this.Active && this.Powered) {
            this.Powered = false;
            this.Active = false;
            this.updateBlockChange();
        }
    }

    private void stateCellUpdate() {
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (!this.Active || this.Powered || this.Disabled) {
            return;
        }
        long l = this.world.getTime();
        if (this.GetInterval() < 20) {
            this.SetInterval(20);
        }
        if (this.timestart > l) {
            this.timestart = l;
        }
        if (this.timestart + this.GetInterval() <= l) {
            this.playSound("random.click", 0.3f, 0.5f, false);
            this.Powered = true;
            this.scheduleTick(2);
            this.updateBlockChange();
        }
    }

    @Override
    public void onBlockNeighborChange(int paramInt) {
        if (this.tryDropBlock()) {
            return;
        }
        switch (this.SubId) {
            case 0: {
                this.timerChange();
                break;
            }
            case 2: {
                this.stateCellChange();
            }
        }
    }

    @Override
    public void onTileTick() {
        switch (this.SubId) {
            case 0: {
                this.timerTick();
                break;
            }
            case 2: {
                this.stateCellTick();
            }
        }
    }

    @Override
    public int getPoweringMask(int paramInt) {
        if (paramInt != 0) {
            return 0;
        }
        switch (this.SubId) {
            case 0: {
                if (this.Disabled || !this.Powered) {
                    return 0;
                }
                return RedPowerLib.mapRotToCon(13, this.Rotation);
            }
            case 1: {
                return RedPowerLib.mapRotToCon(1 << this.PowerState, this.Rotation);
            }
            case 2: {
                int i = (!this.Active || !this.Powered ? 0 : 8) | (!this.Active || this.Powered ? 0 : (this.Deadmap != 0 ? 1 : 4));
                return RedPowerLib.mapRotToCon(i, this.Rotation);
            }
        }
        return 0;
    }

    @Override
    public boolean onPartActivateSide(EntityHuman paramEntityHuman, int paramInt1, int paramInt2) {
        if (paramInt1 != this.Rotation >> 2) {
            return false;
        }
        LogicProxy.displayGuiTimer(this.world, paramEntityHuman, this);
        return true;
    }

    @Override
    public void q_() {
        super.q_();
        switch (this.SubId) {
            case 0: {
                this.timerUpdate();
                break;
            }
            case 1: {
                this.sequencerUpdate();
                break;
            }
            case 2: {
                this.stateCellUpdate();
            }
        }
    }

    @Override
    public float getPointerDirection(float paramFloat) {
        if (this.SubId == 0) {
            if (this.Powered || this.Disabled) {
                return 0.75f;
            }
            long l = this.world.getTime();
            float f = ((float) l + paramFloat - (float) this.timestart) / (float) this.GetInterval();
            if (f > 1.0f) {
                f = 1.0f;
            }
            return f + 0.75f;
        }
        if (this.SubId == 1) {
            long l = this.world.getTime() + 6000;
            float f = ((float) l + paramFloat) / (float) (this.GetInterval() * 4);
            f = this.Deadmap == 1 ? 0.75f - f : (f += 0.75f);
            return f;
        }
        if (this.SubId == 2) {
            if (this.Deadmap > 0) {
                if (!this.Active || this.Disabled) {
                    return 1.0f;
                }
                if (this.Active && this.Powered) {
                    return 0.8f;
                }
            } else {
                if (!this.Active || this.Disabled) {
                    return 0.5f;
                }
                if (this.Active && this.Powered) {
                    return 0.7f;
                }
            }
            long l = this.world.getTime();
            float f = ((float) l + paramFloat - (float) this.timestart) / (float) this.GetInterval();
            if (this.Deadmap > 0) {
                return 1.0f - 0.2f * f;
            }
            return 0.5f + 0.2f * f;
        }
        return 0.0f;
    }

    @Override
    public Quat getOrientationBasis() {
        return MathLib.orientQuat(this.Rotation >> 2, this.Rotation & 3);
    }

    public Vector3 getPointerOrigin() {
        if (this.SubId == 2) {
            if (this.Deadmap > 0) {
                return new Vector3(0.0, -0.1, -0.25);
            }
            return new Vector3(0.0, -0.1, 0.25);
        }
        return new Vector3(0.0, -0.1, 0.0);
    }

    @Override
    public void SetInterval(long paramLong) {
        long intv = paramLong >= this.minInterval ? paramLong : this.minInterval;
        this.intervalk = this.SubId == 0 ? intv - 2 : intv;
    }

    @Override
    public long GetInterval() {
        long intv;
        long l = intv = this.intervalk >= this.minInterval ? this.intervalk : this.minInterval;
        if (this.SubId == 0) {
            return intv + 2;
        }
        return intv;
    }

    @Override
    public int getExtendedID() {
        return 0;
    }

    @Override
    public void a(NBTTagCompound paramNBTTagCompound) {
        super.a(paramNBTTagCompound);
        this.SetInterval(paramNBTTagCompound.getLong("iv"));
        if (this.SubId == 0 || this.SubId == 2) {
            this.timestart = paramNBTTagCompound.getLong("ts");
        }
    }

    @Override
    public void b(NBTTagCompound paramNBTTagCompound) {
        super.b(paramNBTTagCompound);
        paramNBTTagCompound.setLong("iv", this.GetInterval());
        if (this.SubId == 0 || this.SubId == 2) {
            paramNBTTagCompound.setLong("ts", this.timestart);
        }
    }

    @Override
    protected void readFromPacket(Packet211TileDesc paramPacket211TileDesc) throws IOException {
        super.readFromPacket(paramPacket211TileDesc);
        if (paramPacket211TileDesc.subId != 2) {
            return;
        }
        this.SetInterval(Math.max(paramPacket211TileDesc.getUVLC(), (long) RedPowerLogic.minInterval));
        if (this.SubId == 0 || this.SubId == 2) {
            this.timestart = paramPacket211TileDesc.getVLC();
        }
    }

    @Override
    protected void writeToPacket(Packet211TileDesc paramPacket211TileDesc) {
        super.writeToPacket(paramPacket211TileDesc);
        paramPacket211TileDesc.subId = 2;
        paramPacket211TileDesc.addUVLC(this.GetInterval());
        if (this.SubId == 0 || this.SubId == 2) {
            paramPacket211TileDesc.addVLC(this.timestart);
        }
    }
}
