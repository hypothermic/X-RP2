/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.Material
 */
package eloraam.machine;

import eloraam.core.BlockMultipart;
import eloraam.core.CoreLib;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Material;

public class BlockMachinePanel
        extends BlockMultipart {
    public BlockMachinePanel(int n) {
        super(n, Material.STONE);
        this.c(2.0f);
    }

    public int getLightValue(IBlockAccess iBlockAccess, int n, int n2, int n3) {
        TileMachinePanel tileMachinePanel = (TileMachinePanel) CoreLib.getTileEntity(iBlockAccess, n, n2, n3, TileMachinePanel.class);
        if (tileMachinePanel == null) {
            return 0;
        }
        return tileMachinePanel.getLightValue();
    }

    @Override
    public boolean a() {
        return false;
    }

    @Override
    public boolean isACube() {
        return false;
    }

    @Override
    public boolean b() {
        return false;
    }

    @Override
    protected int getDropData(int n) {
        return n;
    }

    public String getTextureFile() {
        return "/eloraam/machine/machine1.png";
    }
}

