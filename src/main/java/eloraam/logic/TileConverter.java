/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 */
package eloraam.logic;

import eloraam.core.CoreLib;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.RedPowerLogic;
import net.minecraft.server.TileEntity;

public abstract class TileConverter
        extends TileEntity {
    public abstract int getBlockSubtype();

    public void q_() {
        this.world.setRawTypeId(this.x, this.y, this.z, 0);
        this.world.setRawTypeIdAndData(this.x, this.y, this.z, RedPowerLogic.blockLogic.id, this.getBlockSubtype() >> 8);
        TileLogic tileLogic = (TileLogic) CoreLib.getTileEntity((IBlockAccess) this.world, this.x, this.y, this.z, TileLogic.class);
        if (tileLogic == null) {
            return;
        }
        tileLogic.initSubType(this.getBlockSubtype() & 255);
    }
}

