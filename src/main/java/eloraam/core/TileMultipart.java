/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class TileMultipart extends TileExtended implements IMultipart {

    @Override
    public boolean isSideSolid(int n) {
        return false;
    }

    @Override
    public boolean isSideNormal(int n) {
        return false;
    }

    @Override
    public List harvestMultipart() {
        ArrayList arrayList = new ArrayList();
        this.addHarvestContents(arrayList);
        this.deleteBlock();
        return arrayList;
    }

    public void addHarvestContents(ArrayList arrayList) {
    }

    public void onHarvestPart(EntityHuman entityHuman, int n) {
    }

    public boolean onPartActivateSide(EntityHuman entityHuman, int n, int n2) {
        return false;
    }

    public float getPartStrength(EntityHuman entityHuman, int n) {
        return 0.0f;
    }

    public abstract boolean blockEmpty();

    public abstract void setPartBounds(BlockMultipart var1, int var2);

    public abstract int getSolidPartsMask();

    public abstract int getPartsMask();

    @Override
    public void breakBlock() {
        List<ItemStack> list = this.harvestMultipart(); // X-RP: added type decl
        for (ItemStack itemStack : list) {
            CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
        }
    }

    public void deleteBlock() {
        BlockMultipart.removeMultipartWithNotify(this.world, this.x, this.y, this.z);
    }
}
