/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.Random;

public class BlockExtended extends BlockContainer {

    private Class[] tileEntityMap = new Class[16];

    public BlockExtended(int n, Material material) {
        super(n, material);
    }

    public boolean a() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public boolean isACube() {
        return false;
    }

    protected int getDropData(int n) {
        return n;
    }

    public void doPhysics(World world, int n, int n2, int n3, int n4) {
        TileExtended tileExtended = (TileExtended) ((Object) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileExtended.class));
        if (tileExtended == null) {
            world.setTypeId(n, n2, n3, 0);
            return;
        }
        tileExtended.onBlockNeighborChange(n4);
    }

    public void postPlace(World world, int n, int n2, int n3, EntityLiving entityLiving) {
        TileExtended tileExtended = (TileExtended) ((Object) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileExtended.class));
        if (tileExtended == null) {
            return;
        }
        tileExtended.onBlockPlacedBy(entityLiving);
    }

    public void remove(World world, int n, int n2, int n3) {
        TileExtended tileExtended = (TileExtended) ((Object) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileExtended.class));
        if (tileExtended == null) {
            return;
        }
        tileExtended.onBlockRemoval();
        super.remove(world, n, n2, n3);
    }

    public boolean d(World world, int n, int n2, int n3, int n4) {
        TileExtended tileExtended = (TileExtended) ((Object) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileExtended.class));
        if (tileExtended == null) {
            return false;
        }
        return tileExtended.isBlockStrongPoweringTo(n4);
    }

    public boolean a(IBlockAccess iBlockAccess, int n, int n2, int n3, int n4) {
        TileExtended tileExtended = (TileExtended) ((Object) CoreLib.getTileEntity(iBlockAccess, n, n2, n3, TileExtended.class));
        if (tileExtended == null) {
            return false;
        }
        return tileExtended.isBlockWeakPoweringTo(n4);
    }

    public boolean interact(World world, int n, int n2, int n3, EntityHuman entityHuman) {
        TileExtended tileExtended = (TileExtended) ((Object) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileExtended.class));
        if (tileExtended == null) {
            return false;
        }
        return tileExtended.onBlockActivated(entityHuman);
    }

    public void a(World world, int n, int n2, int n3, Entity entity) {
        TileExtended tileExtended = (TileExtended) ((Object) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileExtended.class));
        if (tileExtended == null) {
            return;
        }
        tileExtended.onEntityCollidedWithBlock(entity);
    }

    public AxisAlignedBB e(World world, int n, int n2, int n3) {
        AxisAlignedBB axisAlignedBB;
        TileExtended tileExtended = (TileExtended) ((Object) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileExtended.class));
        if (tileExtended != null && (axisAlignedBB = tileExtended.getCollisionBoundingBox()) != null) {
            return axisAlignedBB;
        }
        return super.e(world, n, n2, n3);
    }

    public int c() {
        return RedPowerCore.customBlockModel;
    }

    public void randomDisplayTick(World world, int n, int n2, int n3, Random random) {
        CoreProxy.randomDisplayTick(world, (Block) this, n, n2, n3, random);
    }

    public TileEntity a_() {
        return null;
    }

    public void addTileEntityMapping(int n, Class class_) {
        this.tileEntityMap[n] = class_;
    }

    public void setItemName(int n, String string) {
        Item item = Item.byId[this.id];
        ((ItemExtended) item).setMetaName(n, "tile." + string);
    }

    public TileEntity getBlockEntity(int n) {
        try {
            return (TileEntity) this.tileEntityMap[n].getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception exception) {
            return null;
        }
    }

    public void addCreativeItems(ArrayList arrayList) {
        Item item = Item.byId[this.id];
        item.addCreativeItems(arrayList);
    }
}
