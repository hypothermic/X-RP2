/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.Block;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;

import java.util.ArrayList;

public abstract class TileCoverable extends TileMultipart implements ICoverable, IMultipart {

    @Override
    public abstract boolean canAddCover(int var1, int var2);

    @Override
    public abstract boolean tryAddCover(int var1, int var2);

    @Override
    public abstract int tryRemoveCover(int var1);

    @Override
    public abstract int getCover(int var1);

    @Override
    public abstract int getCoverMask();

    @Override
    public boolean isSideSolid(int n) {
        int n2 = this.getCoverMask();
        return (n2 & 1 << n) > 0;
    }

    @Override
    public boolean isSideNormal(int n) {
        int n2 = this.getCoverMask();
        if ((n2 & 1 << n) == 0) {
            return false;
        }
        int n3 = this.getCover(n);
        int n4 = n3 >> 8;
        return !CoverLib.isTransparent(n3 & 255) && (n4 < 3 || n4 >= 6 && n4 <= 9);
    }

    @Override
    public void addHarvestContents(ArrayList arrayList) {
        if (CoverLib.blockCoverPlate == null) {
            return;
        }
        for (int i = 0; i < 29; ++i) {
            int n = this.getCover(i);
            if (n < 0)
                continue;
            arrayList.add(CoverLib.convertCoverPlate(i, n));
        }
    }

    @Override
    public void onHarvestPart(EntityHuman entityHuman, int n) {
        int n2 = this.tryRemoveCover(n);
        if (n2 < 0) {
            return;
        }
        this.dropCover(n, n2);
        if (this.blockEmpty()) {
            this.deleteBlock();
        }
        RedPowerLib.updateIndirectNeighbors(this.world, this.x, this.y, this.z, Block.REDSTONE_WIRE.id);
    }

    @Override
    public float getPartStrength(EntityHuman entityHuman, int n) {
        int n2 = this.getCover(n);
        if (n2 < 0) {
            return 0.0f;
        }
        ItemStack itemStack = CoverLib.getItemStack(n2 &= 255);
        return Block.byId[itemStack.id].blockStrength(entityHuman, itemStack.getData());
    }

    @Override
    public void setPartBounds(BlockMultipart blockMultipart, int n) {
        int n2 = this.getCover(n);
        float f = CoverLib.getThickness(n, n2);
        switch (n) {
            case 0: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, 1.0f, f, 1.0f);
                break;
            }
            case 1: {
                blockMultipart.a(0.0f, 1.0f - f, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 2: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, f);
                break;
            }
            case 3: {
                blockMultipart.a(0.0f, 0.0f, 1.0f - f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 4: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, f, 1.0f, 1.0f);
                break;
            }
            case 5: {
                blockMultipart.a(1.0f - f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 6: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, f, f, f);
                break;
            }
            case 7: {
                blockMultipart.a(0.0f, 0.0f, 1.0f - f, f, f, 1.0f);
                break;
            }
            case 8: {
                blockMultipart.a(1.0f - f, 0.0f, 0.0f, 1.0f, f, f);
                break;
            }
            case 9: {
                blockMultipart.a(1.0f - f, 0.0f, 1.0f - f, 1.0f, f, 1.0f);
                break;
            }
            case 10: {
                blockMultipart.a(0.0f, 1.0f - f, 0.0f, f, 1.0f, f);
                break;
            }
            case 11: {
                blockMultipart.a(0.0f, 1.0f - f, 1.0f - f, f, 1.0f, 1.0f);
                break;
            }
            case 12: {
                blockMultipart.a(1.0f - f, 1.0f - f, 0.0f, 1.0f, 1.0f, f);
                break;
            }
            case 13: {
                blockMultipart.a(1.0f - f, 1.0f - f, 1.0f - f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 14: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, 1.0f, f, f);
                break;
            }
            case 15: {
                blockMultipart.a(0.0f, 0.0f, 1.0f - f, 1.0f, f, 1.0f);
                break;
            }
            case 16: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, f, f, 1.0f);
                break;
            }
            case 17: {
                blockMultipart.a(1.0f - f, 0.0f, 0.0f, 1.0f, f, 1.0f);
                break;
            }
            case 18: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, f, 1.0f, f);
                break;
            }
            case 19: {
                blockMultipart.a(0.0f, 0.0f, 1.0f - f, f, 1.0f, 1.0f);
                break;
            }
            case 20: {
                blockMultipart.a(1.0f - f, 0.0f, 0.0f, 1.0f, 1.0f, f);
                break;
            }
            case 21: {
                blockMultipart.a(1.0f - f, 0.0f, 1.0f - f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 22: {
                blockMultipart.a(0.0f, 1.0f - f, 0.0f, 1.0f, 1.0f, f);
                break;
            }
            case 23: {
                blockMultipart.a(0.0f, 1.0f - f, 1.0f - f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 24: {
                blockMultipart.a(0.0f, 1.0f - f, 0.0f, f, 1.0f, 1.0f);
                break;
            }
            case 25: {
                blockMultipart.a(1.0f - f, 1.0f - f, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 26: {
                blockMultipart.a(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, 1.0f, 0.5f + f);
                break;
            }
            case 27: {
                blockMultipart.a(0.5f - f, 0.5f - f, 0.0f, 0.5f + f, 0.5f + f, 1.0f);
                break;
            }
            case 28: {
                blockMultipart.a(0.0f, 0.5f - f, 0.5f - f, 1.0f, 0.5f + f, 0.5f + f);
            }
        }
    }

    @Override
    public int getSolidPartsMask() {
        return this.getCoverMask();
    }

    @Override
    public int getPartsMask() {
        return this.getCoverMask();
    }

    public void dropCover(int n, int n2) {
        ItemStack itemStack = CoverLib.convertCoverPlate(n, n2);
        if (itemStack == null) {
            return;
        }
        CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
    }

    public float getExplosionResistance(int n, int n2, Entity entity) {
        int n3 = this.getCover(n);
        if (n3 < 0) {
            return -1.0f;
        }
        ItemStack itemStack = CoverLib.getItemStack(n3 &= 255);
        return Block.byId[itemStack.id].a(entity);
    }
}
