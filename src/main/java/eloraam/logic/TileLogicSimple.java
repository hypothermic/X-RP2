/* X-RP - decompiled with CFR */
package eloraam.logic;

import eloraam.core.RedPowerLib;
import eloraam.logic.TileLogic;
import java.util.Random;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.World;

public class TileLogicSimple extends TileLogic {

    private static final int[] toDead;
    private static final int[] fromDead;
    private static final int[] toDeadNot;
    private static final int[] fromDeadNot;
    private static final int[] toDeadBuf;
    private static final int[] fromDeadBuf;
    private static int[] tickSchedule;

    static {
	int[] arrn = new int[7];
	arrn[1] = 1;
	arrn[2] = 2;
	arrn[3] = 4;
	arrn[4] = 6;
	arrn[5] = 5;
	arrn[6] = 3;
	toDead = arrn;
	int[] arrn2 = new int[7];
	arrn2[1] = 1;
	arrn2[2] = 2;
	arrn2[3] = 6;
	arrn2[4] = 3;
	arrn2[5] = 5;
	arrn2[6] = 4;
	fromDead = arrn2;
	int[] arrn3 = new int[7];
	arrn3[1] = 1;
	arrn3[2] = 8;
	arrn3[3] = 4;
	arrn3[4] = 12;
	arrn3[5] = 5;
	arrn3[6] = 9;
	toDeadNot = arrn3;
	int[] arrn4 = new int[13];
	arrn4[1] = 1;
	arrn4[4] = 3;
	arrn4[5] = 5;
	arrn4[8] = 2;
	arrn4[9] = 6;
	arrn4[12] = 4;
	fromDeadNot = arrn4;
	int[] arrn5 = new int[4];
	arrn5[1] = 1;
	arrn5[2] = 4;
	arrn5[3] = 5;
	toDeadBuf = arrn5;
	int[] arrn6 = new int[6];
	arrn6[1] = 1;
	arrn6[4] = 2;
	arrn6[5] = 3;
	fromDeadBuf = arrn6;
	tickSchedule = new int[] { 2, 4, 6, 8, 16, 32, 64, 128, 256 };
    }

    @Override
    public int getConnectableMask() {
	switch (this.SubId) {
	case 1:
	case 2:
	case 3:
	case 4: {
	    return RedPowerLib.mapRotToCon(8 | 7 & ~this.Deadmap, this.Rotation);
	}
	case 5:
	case 6: {
	    return RedPowerLib.mapRotToCon(13, this.Rotation);
	}
	case 7: {
	    return RedPowerLib.mapRotToCon(10, this.Rotation);
	}
	default: {
	    return super.getConnectableMask();
	}
	case 9: {
	    return RedPowerLib.mapRotToCon(2 | 13 & ~this.Deadmap, this.Rotation);
	}
	case 10: {
	    return RedPowerLib.mapRotToCon(10 | 5 & ~this.Deadmap, this.Rotation);
	}
	case 12:
	}
	return RedPowerLib.mapRotToCon(10, this.Rotation);
    }

    @Override
    public int getExtendedID() {
	return 1;
    }

    @Override
    public int getLightValue() {
	return this.SubId == 16 ? 0 : super.getLightValue();
    }

    @Override
    public int getPartMaxRotation(int arg0, boolean arg1) {
	if (arg1) {
	    switch (this.SubId) {
	    case 0: {
		return 3;
	    }
	    case 1:
	    case 2:
	    case 3:
	    case 4:
	    case 9: {
		return 6;
	    }
	    default: {
		break;
	    }
	    case 10: {
		return 3;
	    }
	    case 11:
	    case 15: {
		return 1;
	    }
	    case 16: {
		return 3;
	    }
	    }
	}
	return super.getPartMaxRotation(arg0, arg1);
    }

    @Override
    public int getPartRotation(int arg0, boolean arg1) {
	if (arg1) {
	    switch (this.SubId) {
	    case 0: {
		return this.Deadmap;
	    }
	    case 1:
	    case 2:
	    case 3:
	    case 4: {
		return fromDead[this.Deadmap];
	    }
	    default: {
		break;
	    }
	    case 9: {
		return fromDeadNot[this.Deadmap];
	    }
	    case 10: {
		return fromDeadBuf[this.Deadmap];
	    }
	    case 11:
	    case 15:
	    case 16: {
		return this.Deadmap;
	    }
	    }
	}
	return super.getPartRotation(arg0, arg1);
    }

    @Override
    public int getPoweringMask(int arg0) {
	if (arg0 != 0) {
	    return 0;
	}
	switch (this.SubId) {
	case 0: {
	    int arg1 = this.Deadmap > 1 ? this.PowerState & 10 : (this.Disabled && !this.Active ? 0 : (this.Active ? (this.Powered ? 4 : 1) : (this.Deadmap == 1 ? (this.Powered ? 6 : 9) : (this.Powered ? 12 : 3))));
	    return RedPowerLib.mapRotToCon(arg1, this.Rotation);
	}
	default: {
	    return super.getPoweringMask(arg0);
	}
	case 8: {
	    if (this.Powered) {
		return RedPowerLib.mapRotToCon(2, this.Rotation);
	    }
	    return RedPowerLib.mapRotToCon(8, this.Rotation);
	}
	case 9:
	case 10: {
	    if (this.Powered) {
		return RedPowerLib.mapRotToCon(13 & ~this.Deadmap, this.Rotation);
	    }
	    return 0;
	}
	case 14: {
	    return RedPowerLib.mapRotToCon((this.Active ? 1 : 0) | (this.Disabled ? 4 : 0) | (this.Powered ? 8 : 0), this.Rotation);
	}
	case 15:
	}
	return RedPowerLib.mapRotToCon(this.Deadmap != 0 ? (this.Powered ? 12 : 0) : (this.Powered ? 9 : 0), this.Rotation);
    }

    @Override
    public void initSubType(int arg0) {
	super.initSubType(arg0);
    }

    private int latch2NextState() {
	if ((this.PowerState & 5) == 0) {
	    return this.PowerState;
	}
	int arg0 = this.PowerState & 5 | 10;
	if (this.Deadmap == 2) {
	    if ((arg0 & 1) > 0) {
		arg0 &= -9;
	    }
	    if ((arg0 & 4) > 0) {
		arg0 &= -3;
	    }
	} else {
	    if ((arg0 & 1) > 0) {
		arg0 &= -3;
	    }
	    if ((arg0 & 4) > 0) {
		arg0 &= -9;
	    }
	}
	return arg0;
    }

    private void latch2Tick() {
	int arg1;
	boolean arg0 = false;
	if (this.PowerState == 0) {
	    this.PowerState |= this.world.random.nextInt(2) != 0 ? 4 : 1;
	    arg0 = true;
	}
	if ((arg1 = this.latch2NextState()) != this.PowerState) {
	    this.PowerState = arg1;
	    arg0 = true;
	}
	if (arg0) {
	    this.updateBlockChange();
	}
	this.latch2UpdatePowerState();
    }

    private void latch2UpdatePowerState() {
	int arg2;
	int arg0 = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 5, this.Rotation, 0);
	boolean arg1 = false;
	if (arg0 != (this.PowerState & 5)) {
	    this.PowerState = this.PowerState & 10 | arg0;
	    arg1 = true;
	}
	if ((arg2 = this.latch2NextState()) != this.PowerState || (this.PowerState & 5) == 0) {
	    this.scheduleTick(2);
	    arg1 = true;
	}
	if (arg1) {
	    this.updateBlock();
	}
    }

    private void latchChange() {
	if (this.Deadmap < 2) {
	    this.latchUpdatePowerState();
	} else {
	    if (this.isTickRunnable()) {
		return;
	    }
	    this.latch2UpdatePowerState();
	}
    }

    private void latchTick() {
	if (this.Active) {
	    this.Active = false;
	    if (this.Disabled) {
		this.updateBlockChange();
		this.scheduleTick(2);
	    } else {
		this.updateBlockChange();
	    }
	} else if (this.Disabled) {
	    int arg0 = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 5, this.Rotation, 0);
	    if (arg0 != this.PowerState) {
		this.updateBlock();
	    }
	    this.PowerState = arg0;
	    switch (arg0) {
	    case 0: {
		this.Disabled = false;
		this.Powered = this.world.random.nextInt(2) == 0;
		this.updateBlockChange();
		break;
	    }
	    case 1: {
		this.Disabled = false;
		this.Powered = false;
		this.updateBlockChange();
		this.playSound("random.click", 0.3f, 0.5f, false);
	    }
	    default: {
		break;
	    }
	    case 4: {
		this.Disabled = false;
		this.Powered = true;
		this.updateBlockChange();
		this.playSound("random.click", 0.3f, 0.5f, false);
		break;
	    }
	    case 5: {
		this.scheduleTick(4);
	    }
	    }
	}
    }

    private void latchUpdatePowerState() {
	if (!this.Disabled || this.Active) {
	    int arg0 = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 5, this.Rotation, 0);
	    if (arg0 != this.PowerState) {
		this.updateBlock();
	    }
	    this.PowerState = arg0;
	    if (!this.isTickRunnable()) {
		if (this.Active) {
		    this.Disabled = arg0 == 5;
		} else if (!(arg0 == 1 && this.Powered || arg0 == 4 && !this.Powered)) {
		    if (arg0 == 5) {
			this.Active = true;
			this.Disabled = true;
			this.Powered = !this.Powered;
			this.scheduleTick(2);
			this.updateBlockChange();
		    }
		} else {
		    this.Powered = !this.Powered;
		    this.Active = true;
		    this.playSound("random.click", 0.3f, 0.5f, false);
		    this.scheduleTick(2);
		    this.updateBlockChange();
		}
	    }
	}
    }

    private void lightTick() {
	int arg0 = this.world.getLightLevel(this.x, this.y, this.z);
	boolean bl = this.Active = arg0 > this.Deadmap * 4;
	if (this.Cover != 7 && this.Cover != 255) {
	    this.Active = false;
	}
	if (this.Active != this.Powered) {
	    this.scheduleTick(2);
	}
	this.simpleTick();
    }

    @Override
    public void onBlockNeighborChange(int arg0) {
	if (!this.tryDropBlock()) {
	    switch (this.SubId) {
	    case 0: {
		this.latchChange();
		break;
	    }
	    case 1:
	    case 2:
	    case 3:
	    case 4:
	    case 5:
	    case 6:
	    case 9:
	    case 10:
	    case 11:
	    case 15:
	    case 16: {
		if (this.isTickRunnable())
		    break;
		this.simpleUpdatePowerState();
		break;
	    }
	    case 7: {
		this.pulseChange();
		break;
	    }
	    case 8: {
		if (this.isTickRunnable())
		    break;
		this.toggleUpdatePowerState();
		break;
	    }
	    case 12: {
		if (this.isTickRunnable())
		    break;
		this.repUpdatePowerState();
		break;
	    }
	    case 13: {
		this.syncChange();
		break;
	    }
	    case 14: {
		if (this.isTickRunnable())
		    break;
		this.randUpdatePowerState();
	    }
	    }
	}
    }

    @Override
    public boolean onPartActivateSide(EntityHuman arg0, int arg1, int arg2) {
	switch (this.SubId) {
	case 8: {
	    if (arg1 != this.Rotation >> 2) {
		return false;
	    }
	    this.playSound("random.click", 0.3f, 0.5f, false);
	    this.Powered = !this.Powered;
	    this.updateBlockChange();
	    return true;
	}
	case 12: {
	    if (arg1 != this.Rotation >> 2) {
		return false;
	    }
	    ++this.Deadmap;
	    if (this.Deadmap > 8) {
		this.Deadmap = 0;
	    }
	    this.updateBlockChange();
	    return true;
	}
	}
	return false;
    }

    @Override
    public void onTileTick() {
	switch (this.SubId) {
	case 0: {
	    if (this.Deadmap < 2) {
		this.latchTick();
		break;
	    }
	    this.latch2Tick();
	    break;
	}
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 9:
	case 10:
	case 11:
	case 15: {
	    this.simpleTick();
	    break;
	}
	case 7: {
	    this.pulseTick();
	    break;
	}
	case 8: {
	    this.toggleTick();
	    break;
	}
	case 12: {
	    this.repTick();
	    break;
	}
	case 13: {
	    this.syncTick();
	    break;
	}
	case 14: {
	    this.randTick();
	    break;
	}
	case 16: {
	    this.lightTick();
	}
	}
    }

    private void pulseChange() {
	int arg0;
	if (this.Active) {
	    int arg02 = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 2, this.Rotation, 0);
	    if (arg02 == 0) {
		this.Active = false;
		this.updateBlock();
	    }
	} else if (!this.Powered && (arg0 = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 2, this.Rotation, 0)) > 0) {
	    this.Powered = true;
	    this.updateBlockChange();
	    this.scheduleTick(2);
	}
    }

    private void pulseTick() {
	if (this.Powered) {
	    this.Powered = false;
	    int arg0 = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 2, this.Rotation, 0);
	    if (arg0 > 0) {
		this.Active = true;
	    }
	    this.updateBlockChange();
	}
    }

    @Override
    public void q_() {
	super.q_();
	if (this.SubId == 16 && !this.isTickScheduled()) {
	    this.scheduleTick(8);
	}
    }

    private void randTick() {
	if ((this.PowerState & 2) != 0) {
	    int arg0 = this.world.random.nextInt(8);
	    this.Disabled = (arg0 & 1) > 0;
	    this.Active = (arg0 & 2) > 0;
	    this.Powered = (arg0 & 4) > 0;
	    this.updateBlockChange();
	    this.randUpdatePowerState();
	    if ((this.PowerState & 2) > 0) {
		this.scheduleTick(4);
	    }
	}
    }

    private void randUpdatePowerState() {
	int arg0 = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 15, this.Rotation, 0);
	int arg1 = arg0 & ~this.PowerState;
	if (arg0 != this.PowerState) {
	    this.updateBlock();
	}
	this.PowerState = arg0;
	if ((arg1 & 2) > 0) {
	    this.scheduleTick(2);
	}
    }

    private void repTick() {
	boolean arg0 = this.simpleWantsPower();
	if (this.Active) {
	    this.Powered = !this.Powered;
	    this.Active = false;
	    this.updateBlockChange();
	    this.repUpdatePowerState();
	}
    }

    private void repUpdatePowerState() {
	if (!this.Active) {
	    boolean arg1;
	    int arg0 = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 2, this.Rotation, 0);
	    if (arg0 != this.PowerState) {
		this.updateBlock();
	    }
	    this.PowerState = arg0;
	    boolean bl = arg1 = this.PowerState > 0;
	    if (arg1 != this.Powered) {
		this.Active = true;
		this.scheduleTick(tickSchedule[this.Deadmap]);
	    }
	}
    }

    @Override
    public void setPartRotation(int arg0, boolean arg1, int arg2) {
	if (arg1) {
	    switch (this.SubId) {
	    case 0: {
		this.Deadmap = arg2;
		this.PowerState = 0;
		this.Active = false;
		this.Powered = false;
		this.updateBlockChange();
		return;
	    }
	    case 1:
	    case 2:
	    case 3:
	    case 4: {
		this.Deadmap = toDead[arg2];
		this.updateBlockChange();
		return;
	    }
	    default: {
		break;
	    }
	    case 9: {
		this.Deadmap = toDeadNot[arg2];
		this.updateBlockChange();
		return;
	    }
	    case 10: {
		this.Deadmap = toDeadBuf[arg2];
		this.updateBlockChange();
		return;
	    }
	    case 11:
	    case 15:
	    case 16: {
		this.Deadmap = arg2;
		this.updateBlockChange();
		return;
	    }
	    }
	}
	super.setPartRotation(arg0, arg1, arg2);
    }

    private void simpleTick() {
	boolean arg0 = this.simpleWantsPower();
	if (this.Powered && !arg0) {
	    this.Powered = false;
	    this.updateBlockChange();
	} else if (!this.Powered && arg0) {
	    this.Powered = true;
	    this.updateBlockChange();
	}
	this.simpleUpdatePowerState();
    }

    private void simpleUpdatePowerState() {
	int arg0 = 15;
	switch (this.SubId) {
	case 2: {
	    arg0 = 7;
	}
	default: {
	    break;
	}
	case 4: {
	    arg0 = 7;
	    break;
	}
	case 5: {
	    arg0 = 5;
	    break;
	}
	case 6: {
	    arg0 = 13;
	    break;
	}
	case 10: {
	    arg0 = 7;
	    break;
	}
	case 11: {
	    arg0 = 7;
	    break;
	}
	case 12: {
	    arg0 = 2;
	    break;
	}
	case 15: {
	    arg0 = (byte) (this.Deadmap != 0 ? 3 : 6);
	    break;
	}
	case 16: {
	    arg0 = 8;
	}
	}
	int arg1 = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, arg0, this.Rotation, 0);
	if (arg1 != this.PowerState) {
	    this.updateBlock();
	}
	this.PowerState = arg1;
	boolean arg2 = this.simpleWantsPower();
	if (arg2 != this.Powered) {
	    this.scheduleTick(2);
	}
    }

    private boolean simpleWantsPower() {
	switch (this.SubId) {
	case 1: {
	    if ((this.PowerState & 7 & ~this.Deadmap) == 0) {
		return true;
	    }
	    return false;
	}
	case 2: {
	    if ((this.PowerState & ~this.Deadmap) > 0) {
		return true;
	    }
	    return false;
	}
	case 3: {
	    if ((this.PowerState & 7 | this.Deadmap) < 7) {
		return true;
	    }
	    return false;
	}
	case 4: {
	    if ((this.PowerState | this.Deadmap) == 7) {
		return true;
	    }
	    return false;
	}
	case 5: {
	    if (this.PowerState != 5 && this.PowerState != 0) {
		return false;
	    }
	    return true;
	}
	case 6: {
	    int arg0 = this.PowerState & 5;
	    if (arg0 != 4 && arg0 != 1) {
		return false;
	    }
	    return true;
	}
	default: {
	    return false;
	}
	case 9: {
	    if ((this.PowerState & 2) == 0) {
		return true;
	    }
	    return false;
	}
	case 10: {
	    if ((this.PowerState & 2) > 0) {
		return true;
	    }
	    return false;
	}
	case 11: {
	    if (this.Deadmap == 0) {
		if ((this.PowerState & 3) != 1 && (this.PowerState & 6) != 6) {
		    return false;
		}
		return true;
	    }
	    if ((this.PowerState & 3) != 3 && (this.PowerState & 6) != 4) {
		return false;
	    }
	    return true;
	}
	case 15: {
	    if ((this.PowerState & 2) == 0) {
		return this.Powered;
	    }
	    if (this.Deadmap == 0) {
		if ((this.PowerState & 4) == 4) {
		    return true;
		}
		return false;
	    }
	    if ((this.PowerState & 1) == 1) {
		return true;
	    }
	    return false;
	}
	case 16:
	}
	return this.Active;
    }

    private void syncChange() {
	int arg0 = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 7, this.Rotation, 0);
	int arg1 = arg0 & ~this.PowerState;
	if (arg0 != this.PowerState) {
	    this.updateBlock();
	}
	this.PowerState = arg0;
	boolean arg2 = false;
	if ((arg0 & 2) == 2) {
	    if (!this.Powered && (this.Active || this.Disabled)) {
		this.Active = false;
		this.Disabled = false;
		arg2 = true;
	    }
	} else {
	    if ((arg1 & 1) > 0 && !this.Active) {
		this.Active = true;
		arg2 = true;
	    }
	    if ((arg1 & 4) > 0 && !this.Disabled) {
		this.Disabled = true;
		arg2 = true;
	    }
	}
	if (arg2) {
	    this.updateBlock();
	    this.scheduleTick(2);
	}
    }

    private void syncTick() {
	if (this.Active && this.Disabled && !this.Powered) {
	    this.Powered = true;
	    this.Active = false;
	    this.Disabled = false;
	    this.scheduleTick(2);
	    this.updateBlockChange();
	} else if (this.Powered) {
	    this.Powered = false;
	    this.updateBlockChange();
	}
    }

    private void toggleTick() {
	if (this.Active) {
	    this.playSound("random.click", 0.3f, 0.5f, false);
	    this.Powered = !this.Powered;
	    this.Active = false;
	    this.updateBlockChange();
	}
	this.toggleUpdatePowerState();
    }

    private void toggleUpdatePowerState() {
	int arg0 = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 5, this.Rotation, 0);
	if (arg0 != this.PowerState) {
	    int arg1 = 5 & arg0 & ~this.PowerState;
	    if (arg1 == 1 || arg1 == 4) {
		this.Active = true;
	    }
	    this.PowerState = arg0;
	    this.updateBlock();
	    if (this.Active) {
		this.scheduleTick(2);
	    }
	}
    }
}
