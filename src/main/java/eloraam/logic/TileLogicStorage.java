/* X-RP - decompiled with CFR */
package eloraam.logic;

import eloraam.core.CoreProxy;
import eloraam.core.Packet211TileDesc;
import eloraam.core.RedPowerLib;
import eloraam.logic.TileLogic;
import java.io.IOException;
import net.minecraft.server.BaseMod;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import net.minecraft.server.mod_RedPowerLogic;

public class TileLogicStorage extends TileLogic {

    LogicStorageModule storage = null;

    @Override
    public int getExtendedID() {
	return 3;
    }

    @Override
    public void initSubType(int n) {
	super.initSubType(n);
	this.initStorage();
    }

    public LogicStorageModule getLogicStorage(Class class_) {
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
	    this.storage = new LogicStorageCounter();
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
    public boolean onPartActivateSide(EntityHuman entityHuman, int n, int n2) {
	if (n != this.Rotation >> 2) {
	    return false;
	}
	if (entityHuman.isSneaking()) {
	    return false;
	}
	if (CoreProxy.isClient(this.world)) {
	    return true;
	}
	switch (this.SubId) {
	case 0: {
	    entityHuman.openGui((BaseMod) mod_RedPowerLogic.instance, 1, this.world, this.x, this.y, this.z);
	    return true;
	}
	}
	return true;
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

    public abstract class LogicStorageModule {

	public abstract void updatePowerState();

	public abstract void tileTick();

	public abstract int getSubType();

	public abstract int getPoweringMask(int var1);

	public abstract void readFromNBT(NBTTagCompound var1);

	public abstract void writeToNBT(NBTTagCompound var1);

	public void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
	}

	public void writeToPacket(Packet211TileDesc packet211TileDesc) {
	}
    }

    public class LogicStorageCounter extends LogicStorageModule {

	public int Count;
	public int CountMax;
	public int Inc;
	public int Dec;

	@Override
	public void updatePowerState() {
	    int n = RedPowerLib.getRotPowerState((IBlockAccess) TileLogicStorage.this.world, TileLogicStorage.this.x, TileLogicStorage.this.y, TileLogicStorage.this.z, 5, TileLogicStorage.this.Rotation, 0);
	    if (n != TileLogicStorage.this.PowerState) {
		if ((n & ~TileLogicStorage.this.PowerState & 1) > 0) {
		    TileLogicStorage.this.Active = true;
		}
		if ((n & ~TileLogicStorage.this.PowerState & 4) > 0) {
		    TileLogicStorage.this.Disabled = true;
		}
		TileLogicStorage.this.PowerState = n;
		TileLogicStorage.this.updateBlock();
		if (TileLogicStorage.this.Active || TileLogicStorage.this.Disabled) {
		    TileLogicStorage.this.scheduleTick(2);
		}
	    }
	}

	@Override
	public void tileTick() {
	    int n = this.Count;
	    if (TileLogicStorage.this.Deadmap > 0) {
		if (TileLogicStorage.this.Active) {
		    this.Count -= this.Dec;
		    TileLogicStorage.this.Active = false;
		}
		if (TileLogicStorage.this.Disabled) {
		    this.Count += this.Inc;
		    TileLogicStorage.this.Disabled = false;
		}
	    } else {
		if (TileLogicStorage.this.Active) {
		    this.Count += this.Inc;
		    TileLogicStorage.this.Active = false;
		}
		if (TileLogicStorage.this.Disabled) {
		    this.Count -= this.Dec;
		    TileLogicStorage.this.Disabled = false;
		}
	    }
	    if (this.Count < 0) {
		this.Count = 0;
	    }
	    if (this.Count > this.CountMax) {
		this.Count = this.CountMax;
	    }
	    if (n != this.Count) {
		TileLogicStorage.this.updateBlockChange();
		TileLogicStorage.this.playSound("random.click", 0.3f, 0.5f, false);
	    }
	    this.updatePowerState();
	}

	@Override
	public int getSubType() {
	    return 0;
	}

	@Override
	public int getPoweringMask(int n) {
	    int n2 = 0;
	    if (n != 0) {
		return 0;
	    }
	    if (this.Count == 0) {
		n2 |= 2;
	    }
	    if (this.Count == this.CountMax) {
		n2 |= 8;
	    }
	    return RedPowerLib.mapRotToCon(n2, TileLogicStorage.this.Rotation);
	}

	@Override
	public void readFromNBT(NBTTagCompound nBTTagCompound) {
	    this.Count = nBTTagCompound.getInt("cnt");
	    this.CountMax = nBTTagCompound.getInt("max");
	    this.Inc = nBTTagCompound.getInt("inc");
	    this.Dec = nBTTagCompound.getInt("dec");
	}

	@Override
	public void writeToNBT(NBTTagCompound nBTTagCompound) {
	    nBTTagCompound.setInt("cnt", this.Count);
	    nBTTagCompound.setInt("max", this.CountMax);
	    nBTTagCompound.setInt("inc", this.Inc);
	    nBTTagCompound.setInt("dec", this.Dec);
	}

	@Override
	public void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
	    this.Count = (int) packet211TileDesc.getUVLC();
	    this.CountMax = (int) packet211TileDesc.getUVLC();
	}

	@Override
	public void writeToPacket(Packet211TileDesc packet211TileDesc) {
	    packet211TileDesc.addUVLC(this.Count);
	    packet211TileDesc.addUVLC(this.CountMax);
	}

	public LogicStorageCounter() {
	    this.Count = 0;
	    this.CountMax = 10;
	    this.Inc = 1;
	    this.Dec = 1;
	}
    }

}
