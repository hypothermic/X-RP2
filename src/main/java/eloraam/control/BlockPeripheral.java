/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.core.BlockExtended;
import net.minecraft.server.Material;
import net.minecraft.server.World;

public class BlockPeripheral extends BlockExtended {

    public BlockPeripheral(int n) {
        super(n, Material.STONE);
        this.c(2.0f);
    }

    @Override
    public boolean a() {
        return true;
    }

    @Override
    public boolean isACube() {
        return true;
    }

    @Override
    public boolean b() {
        return true;
    }

    public boolean isBlockNormalCube(World world, int n, int n2, int n3) {
        return false;
    }

    public boolean isBlockSolidOnSide(World world, int n, int n2, int n3, int n4) {
        return true;
    }

    @Override
    protected int getDropData(int n) {
        return n;
    }
}
