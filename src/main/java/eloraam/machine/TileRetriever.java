/* X-RP - decompiled with CFR */
package eloraam.machine;

import eloraam.core.BluePowerConductor;
import eloraam.core.BluePowerEndpoint;
import eloraam.core.CoreLib;
import eloraam.core.CoreProxy;
import eloraam.core.IBluePowerConnectable;
import eloraam.core.ITubeConnectable;
import eloraam.core.MachineLib;
import eloraam.core.RedPowerLib;
import eloraam.core.TubeBuffer;
import eloraam.core.TubeItem;
import eloraam.core.TubeLib;
import eloraam.core.WorldCoord;
import eloraam.machine.TileFilter;
import eloraam.machine.TileTube;
import forge.ISidedInventory;
import java.util.List;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.BaseMod;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityMinecart;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import net.minecraft.server.mod_RedPowerMachine;

public class TileRetriever
extends TileFilter
implements IBluePowerConnectable {
    BluePowerEndpoint cond;
    public int ConMask;
    public byte select;
    public byte mode;

    public TileRetriever() {
        this.cond = new BluePowerEndpoint(){

            @Override
            public TileEntity getParent() {
                return TileRetriever.this;
            }
        };
        this.ConMask = -1;
        this.select = 0;
        this.mode = 0;
    }

    @Override
    public int getConnectableMask() {
        return 1073741823;
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
    public boolean tubeItemEnter(int n, int n2, TubeItem tubeItem) {
        if (n == (this.Rotation ^ 1) && n2 == 3) {
            if (!this.buffer.isEmpty()) {
                return false;
            }
            if (this.filterMap == null) {
                this.regenFilterMap();
            }
            if (this.filterMap.size() > 0 && !this.filterMap.containsKey(tubeItem.item)) {
                return false;
            }
            this.buffer.addNewColor(tubeItem.item, this.color);
            this.Delay = true;
            this.updateBlock();
            this.scheduleTick(5);
            this.drainBuffer();
            return true;
        }
        if (n == this.Rotation && n2 == 2) {
            return super.tubeItemEnter(n, n2, tubeItem);
        }
        return false;
    }

    @Override
    public boolean tubeItemCanEnter(int n, int n2, TubeItem tubeItem) {
        if (n == (this.Rotation ^ 1) && n2 == 3) {
            if (!this.buffer.isEmpty()) {
                return false;
            }
            if (this.filterMap == null) {
                this.regenFilterMap();
            }
            if (this.filterMap.size() == 0) {
                return true;
            }
            return this.filterMap.containsKey(tubeItem.item);
        }
        if (n == this.Rotation && n2 == 2) {
            return super.tubeItemCanEnter(n, n2, tubeItem);
        }
        return false;
    }

    private void stepSelect() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack;
            this.select = (byte)(this.select + 1);
            if (this.select > 8) {
                this.select = 0;
            }
            if ((itemStack = this.contents[this.select]) == null || itemStack.count <= 0) continue;
            return;
        }
        this.select = 0;
    }

    // X-RP: modified this method quite a bit.
    @Override
    protected boolean handleExtract(WorldCoord worldCoord) {
        Object object;
        int n;
        ITubeConnectable iTubeConnectable = (ITubeConnectable)CoreLib.getTileEntity((IBlockAccess)this.world, worldCoord, ITubeConnectable.class);
        if (iTubeConnectable == null || !iTubeConnectable.canRouteItems()) {
            return super.handleExtract(worldCoord);
        }
        if (this.cond.getVoltage() < 60.0) {
            return false;
        }
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        TubeLib.InRouteFinder inRouteFinder = new TubeLib.InRouteFinder(this.world, this.filterMap);
        if (this.mode == 0) {
            inRouteFinder.setSubFilt(this.select);
        }
        if ((n = inRouteFinder.find(new WorldCoord(this), 1 << (this.Rotation ^ 1))) < 0) {
            return false;
        }
        WorldCoord worldCoord2 = inRouteFinder.getResultPoint();
        IInventory iInventory = MachineLib.getInventory(this.world, worldCoord2);
        if (iInventory == null) {
            return false;
        }
        int n2 = inRouteFinder.getResultSide();
        int n3 = 0;
        int n4 = iInventory.getSize();
        if (iInventory instanceof ISidedInventory) {
            object = (ISidedInventory)iInventory;
            n3 = ((ISidedInventory) object).getStartInventorySide(n2);
            n4 = ((ISidedInventory) object).getSizeInventorySide(n2);
        }
        worldCoord2.step(n2);
        object = (TileTube)CoreLib.getTileEntity((IBlockAccess)this.world, worldCoord2, TileTube.class);
        if (object == null) {
            return false;
        }
        ItemStack itemStack = MachineLib.collectOneStack(iInventory, n3, n4, this.contents[n]);
        if (itemStack == null) {
            return false;
        }
        TubeItem tubeItem = new TubeItem(n2, itemStack);
        this.cond.drawPower(25 * itemStack.count);
        tubeItem.mode = 3;
        ((TileTube) object).addTubeItem(tubeItem); // X-RP: re-check this cast.
        if (this.mode == 0) {
            this.stepSelect();
        }
        return true;
    }

    @Override
    public void q_() {
        super.q_();
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (this.ConMask < 0) {
            this.ConMask = RedPowerLib.getConnections((IBlockAccess)this.world, this, this.x, this.y, this.z);
            this.cond.recache(this.ConMask, 0);
        }
        this.cond.iterate();
        this.dirtyBlock();
        if (this.cond.Flow == 0) {
            if (this.Charged) {
                this.Charged = false;
                this.updateBlock();
            }
        } else if (!this.Charged) {
            this.Charged = true;
            this.updateBlock();
        }
    }

    @Override
    public void onBlockNeighborChange(int n) {
        this.ConMask = -1;
        super.onBlockNeighborChange(n);
    }

    @Override
    public void onTileTick() {
        super.onTileTick();
        if (this.Delay) {
            this.Delay = false;
            this.updateBlock();
        }
    }

    @Override
    protected void doSuck() {
        this.suckEntities(this.getSizeBox(2.55, 5.05, -0.95));
    }

    @Override
    protected boolean suckFilter(ItemStack itemStack) {
        if (this.cond.getVoltage() < 60.0) {
            return false;
        }
        if (!super.suckFilter(itemStack)) {
            return false;
        }
        this.cond.drawPower(25 * itemStack.count);
        return true;
    }

    @Override
    protected int suckEntity(Object object) {
        if (object instanceof EntityMinecart) {
            EntityMinecart entityMinecart;
            if (this.cond.getVoltage() < 60.0) {
                return 0;
            }
            if (this.filterMap == null) {
                this.regenFilterMap();
            }
            if (!MachineLib.emptyInventory((IInventory)(entityMinecart = (EntityMinecart)object), 0, entityMinecart.getSize())) {
                return super.suckEntity(object);
            }
            List<ItemStack> list = entityMinecart.getItemsDropped(); // X-RP: added type decl
            for (ItemStack itemStack : list) {
                this.buffer.addNewColor(itemStack, this.color);
            }
            entityMinecart.die();
            this.cond.drawPower(200.0);
            return 2;
        }
        return super.suckEntity(object);
    }

    @Override
    public boolean onBlockActivated(EntityHuman entityHuman) {
        if (entityHuman.isSneaking()) {
            return false;
        }
        if (CoreProxy.isClient(this.world)) {
            return true;
        }
        entityHuman.openGui((BaseMod)mod_RedPowerMachine.instance, 7, this.world, this.x, this.y, this.z);
        return true;
    }

    @Override
    public int getExtendedID() {
        return 8;
    }

    @Override
    public String getName() {
        return "Retriever";
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        this.cond.readFromNBT(nBTTagCompound);
        this.mode = nBTTagCompound.getByte("mode");
        this.select = nBTTagCompound.getByte("sel");
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        this.cond.writeToNBT(nBTTagCompound);
        nBTTagCompound.setByte("mode", this.mode);
        nBTTagCompound.setByte("sel", this.select);
    }

}

