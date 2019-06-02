/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.AxisAlignedBB
 *  net.minecraft.server.Entity
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.EntityLiving
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.MovingObjectPosition
 *  net.minecraft.server.PlayerInventory
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.Vec3D
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.IConnectable;
import eloraam.core.IFrameLink;
import eloraam.core.RedPowerLib;
import eloraam.core.WorldCoord;
import net.minecraft.server.*;

import java.util.List;

public abstract class TileDeployBase
        extends TileMachine
        implements IFrameLink,
        IConnectable {
    protected static EntityHuman fakePlayer = null;

    @Override
    public boolean isFrameMoving() {
        return false;
    }

    @Override
    public boolean canFrameConnectIn(int n) {
        return n != (this.Rotation ^ 1);
    }

    @Override
    public boolean canFrameConnectOut(int n) {
        return false;
    }

    @Override
    public WorldCoord getFrameLinkset() {
        return null;
    }

    @Override
    public int getConnectableMask() {
        return 1073741823 ^ RedPowerLib.getConDirMask(this.Rotation ^ 1);
    }

    @Override
    public int getConnectClass(int n) {
        return 0;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public int getBlockID() {
        return RedPowerMachine.blockMachine.id;
    }

    protected void initPlayer() {
        float f;
        float f2;
        if (fakePlayer == null) {
            fakePlayer = FakePlayer.get(this.world);
        }
        double d = (double) this.x + 0.5;
        double d2 = (double) this.y - 1.1;
        double d3 = (double) this.z + 0.5;
        switch (this.Rotation) {
            case 0: {
                f = -90.0f;
                f2 = 0.0f;
                d2 += 0.51;
                break;
            }
            case 1: {
                f = 90.0f;
                f2 = 0.0f;
                d2 -= 0.51;
                break;
            }
            case 2: {
                f = 0.0f;
                f2 = 0.0f;
                d3 += 0.51;
                break;
            }
            case 3: {
                f = 0.0f;
                f2 = 180.0f;
                d3 -= 0.51;
                break;
            }
            case 4: {
                f = 0.0f;
                f2 = 270.0f;
                d += 0.51;
                break;
            }
            default: {
                f = 0.0f;
                f2 = 90.0f;
                d -= 0.51;
            }
        }
        TileDeployBase.fakePlayer.world = this.world;
        fakePlayer.setPositionRotation(d, d2, d3, f2, f);
    }

    protected static Entity traceEntities(World world, Entity entity, Vec3D vec3D, Vec3D vec3D2) {
        AxisAlignedBB axisAlignedBB = AxisAlignedBB.b((double) vec3D.a, (double) vec3D.b, (double) vec3D.c, (double) vec3D.a, (double) vec3D.b, (double) vec3D.c);
        List list = world.getEntities(entity, axisAlignedBB.a(vec3D2.a, vec3D2.b, vec3D2.c).grow(1.0, 1.0, 1.0));
        Vec3D vec3D3 = vec3D.add(vec3D2.a, vec3D2.b, vec3D2.c);
        Entity entity2 = null;
        double d = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            double d2;
            Entity entity3 = (Entity) list.get(i);
            if (!entity3.o_()) continue;
            float f = entity3.j_();
            AxisAlignedBB axisAlignedBB2 = entity3.boundingBox.grow((double) f, (double) f, (double) f);
            if (axisAlignedBB2.a(vec3D)) {
                entity2 = entity3;
                d = 0.0;
                break;
            }
            MovingObjectPosition movingObjectPosition = axisAlignedBB2.a(vec3D, vec3D3);
            if (movingObjectPosition == null || (d2 = vec3D.b(movingObjectPosition.pos)) >= d && d != 0.0) continue;
            entity2 = entity3;
            d = d2;
        }
        return entity2;
    }

    protected boolean useOnEntity(Entity entity) {
        if (entity.b(fakePlayer)) {
            return true;
        }
        ItemStack itemStack = fakePlayer.U();
        if (itemStack != null && entity instanceof EntityLiving) {
            int n = itemStack.count;
            itemStack.a((EntityLiving) entity);
            if (itemStack.count != n) {
                return true;
            }
        }
        return false;
    }

    protected boolean tryUseItemStack(ItemStack itemStack, int n, int n2, int n3, int n4) {
        TileDeployBase.fakePlayer.inventory.itemInHandIndex = n4;
        if (itemStack.id == Item.INK_SACK.id || itemStack.id == Item.MINECART.id || itemStack.id == Item.POWERED_MINECART.id || itemStack.id == Item.STORAGE_MINECART.id) {
            if (itemStack.getItem().interactWith(itemStack, fakePlayer, this.world, n, n2, n3, 1)) {
                return true;
            }
        } else {
            if (itemStack.getItem().onItemUseFirst(itemStack, fakePlayer, this.world, n, n2, n3, 1)) {
                return true;
            }
            if (itemStack.getItem().interactWith(itemStack, fakePlayer, this.world, n, n2 - 1, n3, 1)) {
                return true;
            }
        }
        int n5 = itemStack.count;
        ItemStack itemStack2 = itemStack.a(this.world, fakePlayer);
        if (itemStack2 != itemStack || itemStack2 != null && itemStack2.count != n5) {
            itemStack = itemStack2;
            TileDeployBase.fakePlayer.inventory.setItem(n4, itemStack2);
            return true;
        }
        Vec3D vec3D = fakePlayer.f(1.0f);
        vec3D.a *= 2.5;
        vec3D.b *= 2.5;
        vec3D.c *= 2.5;
        Vec3D vec3D2 = Vec3D.create((double) ((double) this.x + 0.5), (double) ((double) this.y + 0.5), (double) ((double) this.z + 0.5));
        Entity entity = TileDeployBase.traceEntities(this.world, (Entity) fakePlayer, vec3D2, vec3D);
        return entity != null && this.useOnEntity(entity);
    }

    public abstract void enableTowards(WorldCoord var1);

    @Override
    public void onBlockNeighborChange(int n) {
        int n2 = this.getConnectableMask();
        if (!RedPowerLib.isPowered((IBlockAccess) this.world, this.x, this.y, this.z, n2, n2 >> 24)) {
            if (!this.Active) {
                return;
            }
            this.scheduleTick(5);
            return;
        }
        if (this.Active) {
            return;
        }
        this.Active = true;
        this.updateBlock();
        WorldCoord worldCoord = new WorldCoord(this);
        worldCoord.step(this.Rotation ^ 1);
        this.enableTowards(worldCoord);
    }

    @Override
    public void onTileTick() {
        this.Active = false;
        this.updateBlock();
    }
}

