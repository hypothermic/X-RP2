/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.Material
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.BlockExtended;
import eloraam.core.CoreLib;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Material;
import net.minecraft.server.World;

public class BlockMachine
        extends BlockExtended {
    public BlockMachine(int n) {
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

    public boolean isPowerSource() {
        return true;
    }

    @Override
    public boolean a(IBlockAccess iBlockAccess, int n, int n2, int n3, int n4) {
        TileMachine tileMachine = (TileMachine) CoreLib.getTileEntity(iBlockAccess, n, n2, n3, TileMachine.class);
        if (tileMachine == null) {
            return false;
        }
        if (tileMachine.getExtendedID() != 4 && tileMachine.getExtendedID() != 10) {
            return false;
        }
        return tileMachine.isPoweringTo(n4);
    }

    public boolean isFireSource(World world, int n, int n2, int n3, int n4, int n5) {
        if (n4 != 12) {
            return false;
        }
        TileIgniter tileIgniter = (TileIgniter) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileIgniter.class);
        if (tileIgniter == null) {
            return false;
        }
        return tileIgniter.isOnFire(n5);
    }

    public String getTextureFile() {
        return "/eloraam/machine/machine1.png";
    }
}

