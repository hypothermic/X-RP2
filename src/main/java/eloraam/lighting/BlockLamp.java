/* X-RP - decompiled with CFR */
package eloraam.lighting;

import eloraam.core.CoreLib;
import eloraam.core.RedPowerLib;
import forge.IMultipassRender;
import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.Random;

public class BlockLamp extends Block implements IMultipassRender {

    public boolean lit;

    public BlockLamp(int n, boolean bl) {
        super(n, CoreLib.materialRedpower);
        this.a(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        this.c(0.5f);
        this.lit = bl;
    }

    public boolean canRenderInPass(int n) {
        return true;
    }

    public boolean a() {
        return true;
    }

    public boolean b() {
        return true;
    }

    public boolean isACube() {
        return true;
    }

    public boolean isPowerSource() {
        return true;
    }

    public int getRenderBlockPass() {
        return 1;
    }

    protected int getDropData(int n) {
        return n;
    }

    public int getDropType(int n, Random random, int n2) {
        return RedPowerLighting.blockLampOff.id;
    }

    private void checkPowerState(World world, int n, int n2, int n3) {
        if (!this.lit && RedPowerLib.isPowered((IBlockAccess) world, n, n2, n3, 16777215, 63)) {
            int n4 = world.getData(n, n2, n3);
            world.setTypeIdAndData(n, n2, n3, RedPowerLighting.blockLampOn.id, n4);
            world.notify(n, n2, n3);
        } else if (this.lit && !RedPowerLib.isPowered((IBlockAccess) world, n, n2, n3, 16777215, 63)) {
            int n5 = world.getData(n, n2, n3);
            world.setTypeIdAndData(n, n2, n3, RedPowerLighting.blockLampOff.id, n5);
            world.notify(n, n2, n3);
        }
    }

    public void doPhysics(World world, int n, int n2, int n3, int n4) {
        this.checkPowerState(world, n, n2, n3);
    }

    public void onPlace(World world, int n, int n2, int n3) {
        this.checkPowerState(world, n, n2, n3);
    }

    public int c() {
        return RedPowerCore.customBlockModel;
    }

    public void addCreativeItems(ArrayList arrayList) {
        if (this.lit) {
            return;
        }
        for (int i = 0; i < 16; ++i) {
            arrayList.add(new ItemStack((Block) this, 1, i));
        }
    }
}
