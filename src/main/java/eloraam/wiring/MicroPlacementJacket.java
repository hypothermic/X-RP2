/* X-RP - decompiled with CFR */
package eloraam.wiring;

import eloraam.core.CoreLib;
import eloraam.core.CoverLib;
import eloraam.core.IMicroPlacement;
import eloraam.core.RedPowerLib;
import eloraam.core.WorldCoord;
import eloraam.wiring.TileWiring;
import java.util.ArrayList;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class MicroPlacementJacket implements IMicroPlacement {

    private void blockUsed(World world, WorldCoord worldCoord, ItemStack itemStack) {
	--itemStack.count;
	CoreLib.placeNoise(world, worldCoord.x, worldCoord.y, worldCoord.z, itemStack.id);
	world.notify(worldCoord.x, worldCoord.y, worldCoord.z);
	RedPowerLib.updateIndirectNeighbors(world, worldCoord.x, worldCoord.y, worldCoord.z, itemStack.id);
    }

    private int getWireMeta(int n) {
	switch (n) {
	case 64: {
	    return 1;
	}
	case 65: {
	    return 3;
	}
	case 66: {
	    return 5;
	}
	}
	return 0;
    }

    private boolean initialPlace(ItemStack itemStack, EntityHuman entityHuman, World world, WorldCoord worldCoord, int n) {
	int n2 = itemStack.getData() >> 8;
	int n3 = itemStack.id;
	n2 = this.getWireMeta(n2);
	if (!world.mayPlace(n3, worldCoord.x, worldCoord.y, worldCoord.z, false, n)) {
	    return false;
	}
	if (!world.setRawTypeIdAndData(worldCoord.x, worldCoord.y, worldCoord.z, n3, n2)) {
	    return true;
	}
	TileWiring tileWiring = (TileWiring) CoreLib.getTileEntity((IBlockAccess) world, worldCoord, TileWiring.class);
	if (tileWiring == null) {
	    return false;
	}
	tileWiring.CenterPost = (short) (itemStack.getData() & 255);
	tileWiring.ConSides |= 64;
	this.blockUsed(world, worldCoord, itemStack);
	return true;
    }

    private boolean tryAddingJacket(World world, WorldCoord worldCoord, ItemStack itemStack) {
	TileWiring tileWiring = (TileWiring) CoreLib.getTileEntity((IBlockAccess) world, worldCoord, TileWiring.class);
	if (tileWiring == null) {
	    return false;
	}
	if ((tileWiring.ConSides & 64) > 0) {
	    return false;
	}
	if (!CoverLib.checkPlacement(tileWiring.CoverSides, tileWiring.Covers, tileWiring.ConSides, true)) {
	    return false;
	}
	tileWiring.CenterPost = (short) (itemStack.getData() & 255);
	tileWiring.ConSides |= 64;
	tileWiring.uncache();
	this.blockUsed(world, worldCoord, itemStack);
	return true;
    }

    @Override
    public boolean onPlaceMicro(ItemStack itemStack, EntityHuman entityHuman, World world, WorldCoord worldCoord, int n) {
	int n2 = itemStack.getData();
	int n3 = n2 & 255;
	n2 >>= 8;
	int n4 = (n2 = this.getWireMeta(n2)) << 8;
	if (CoverLib.tryMakeCompatible(world, worldCoord, itemStack.id, n4) && this.tryAddingJacket(world, worldCoord, itemStack)) {
	    return true;
	}
	worldCoord.step(n);
	int n5 = world.getTypeId(worldCoord.x, worldCoord.y, worldCoord.z);
	if (n5 != itemStack.id) {
	    return this.initialPlace(itemStack, entityHuman, world, worldCoord, n);
	}
	if (!CoverLib.tryMakeCompatible(world, worldCoord, itemStack.id, n4)) {
	    return false;
	}
	return this.tryAddingJacket(world, worldCoord, itemStack);
    }

    @Override
    public String getMicroName(int n, int n2) {
	switch (n) {
	case 64: {
	    String string = CoverLib.getName(n2);
	    if (string == null) {
		return null;
	    }
	    if (CoverLib.isTransparent(n2)) {
		return null;
	    }
	    return "tile.rparmwire." + string;
	}
	case 65: {
	    String string = CoverLib.getName(n2);
	    if (string == null) {
		return null;
	    }
	    if (CoverLib.isTransparent(n2)) {
		return null;
	    }
	    return "tile.rparmcable." + string;
	}
	case 66: {
	    String string = CoverLib.getName(n2);
	    if (string == null) {
		return null;
	    }
	    if (CoverLib.isTransparent(n2)) {
		return null;
	    }
	    return "tile.rparmbwire." + string;
	}
	}
	return null;
    }

    @Override
    public void addCreativeItems(int n, ArrayList arrayList) {
    }
}
