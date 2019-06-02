/* X-RP - decompiled with CFR */
package eloraam.base;

import eloraam.core.BlockExtended;
import eloraam.core.CoreLib;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Material;

public class BlockAppliance extends BlockExtended {

    public BlockAppliance(int n) {
        super(n, Material.STONE);
        this.c(2.0f);
    }

    public int getLightValue(IBlockAccess iBlockAccess, int n, int n2, int n3) {
        TileAppliance tileAppliance = (TileAppliance) CoreLib.getTileEntity(iBlockAccess, n, n2, n3, TileAppliance.class);
        if (tileAppliance == null) {
            return super.getLightValue(iBlockAccess, n, n2, n3);
        }
        return !tileAppliance.Active ? 0 : 13;
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

    @Override
    protected int getDropData(int n) {
        return n;
    }
}
