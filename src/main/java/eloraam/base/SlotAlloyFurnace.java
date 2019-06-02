/* X-RP - decompiled with CFR */
package eloraam.base;

import eloraam.core.AchieveLib;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Slot;

public class SlotAlloyFurnace extends Slot {

    private EntityHuman thePlayer;
    int totalCrafted;

    public SlotAlloyFurnace(EntityHuman entityHuman, IInventory iInventory, int n, int n2, int n3) {
        super(iInventory, n, n2, n3);
        this.thePlayer = entityHuman;
    }

    public boolean isAllowed(ItemStack itemStack) {
        return false;
    }

    public ItemStack a(int n) {
        if (this.c()) {
            this.totalCrafted += Math.min(n, this.getItem().count);
        }
        return super.a(n);
    }

    public void c(ItemStack itemStack) {
        this.func_48416_b(itemStack);
        super.c(itemStack);
    }

    protected void func_48415_a(ItemStack itemStack, int n) {
        this.totalCrafted += n;
        this.func_48416_b(itemStack);
    }

    protected void func_48416_b(ItemStack itemStack) {
        itemStack.a(this.thePlayer.world, this.thePlayer, this.totalCrafted);
        this.totalCrafted = 0;
        AchieveLib.onAlloy(this.thePlayer, itemStack);
    }
}
