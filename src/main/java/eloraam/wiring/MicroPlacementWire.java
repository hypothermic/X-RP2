/* X-RP - decompiled with CFR */
package eloraam.wiring;

import eloraam.core.CoreLib;
import eloraam.core.CoverLib;
import eloraam.core.IMicroPlacement;
import eloraam.core.RedPowerLib;
import eloraam.core.TileCovered;
import eloraam.core.WorldCoord;
import eloraam.wiring.TileWiring;
import java.util.ArrayList;
import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class MicroPlacementWire implements IMicroPlacement {

    private void blockUsed(World world, WorldCoord worldCoord, ItemStack itemStack) {
	--itemStack.count;
	CoreLib.placeNoise(world, worldCoord.x, worldCoord.y, worldCoord.z, itemStack.id);
	world.notify(worldCoord.x, worldCoord.y, worldCoord.z);
	RedPowerLib.updateIndirectNeighbors(world, worldCoord.x, worldCoord.y, worldCoord.z, itemStack.id);
    }

    private boolean initialPlace(ItemStack itemStack, EntityHuman entityHuman, World world, WorldCoord worldCoord, int n) {
	int n2 = itemStack.getData() >> 8;
	int n3 = itemStack.id;
	if (!world.mayPlace(n3, worldCoord.x, worldCoord.y, worldCoord.z, false, n)) {
	    return false;
	}
	if (!RedPowerLib.canSupportWire((IBlockAccess) world, worldCoord.x, worldCoord.y, worldCoord.z, n ^ 1)) {
	    return false;
	}
	if (!world.setRawTypeIdAndData(worldCoord.x, worldCoord.y, worldCoord.z, n3, n2)) {
	    return true;
	}
	TileWiring tileWiring = (TileWiring) CoreLib.getTileEntity((IBlockAccess) world, worldCoord, TileWiring.class);
	if (tileWiring == null) {
	    return false;
	}
	tileWiring.ConSides = 1 << (n ^ 1);
	tileWiring.Metadata = itemStack.getData() & 255;
	this.blockUsed(world, worldCoord, itemStack);
	return true;
    }

    @Override
    public boolean onPlaceMicro(ItemStack itemStack, EntityHuman entityHuman, World world, WorldCoord worldCoord, int n) {
	worldCoord.step(n);
	int n2 = world.getTypeId(worldCoord.x, worldCoord.y, worldCoord.z);
	if (n2 != itemStack.id) {
	    return this.initialPlace(itemStack, entityHuman, world, worldCoord, n);
	}
	TileCovered tileCovered = (TileCovered) CoreLib.getTileEntity((IBlockAccess) world, worldCoord, TileCovered.class);
	if (tileCovered == null) {
	    return false;
	}
	int n3 = 1 << (n ^ 1);
	if ((tileCovered.CoverSides & n3) > 0) {
	    return false;
	}
	int n4 = itemStack.getData();
	int n5 = n4 & 255;
	n4 >>= 8;
	if (!CoverLib.tryMakeCompatible(world, worldCoord, itemStack.id, itemStack.getData())) {
	    return false;
	}
	TileWiring tileWiring = (TileWiring) CoreLib.getTileEntity((IBlockAccess) world, worldCoord, TileWiring.class);
	if (tileWiring == null) {
	    return false;
	}
	if (!RedPowerLib.canSupportWire((IBlockAccess) world, worldCoord.x, worldCoord.y, worldCoord.z, n ^ 1)) {
	    return false;
	}
	if (((tileWiring.ConSides | tileWiring.CoverSides) & n3) > 0) {
	    return false;
	}
	int n6 = (n3 |= tileWiring.ConSides) & 63;
	if (n6 == 3 || n6 == 12 || n6 == 48) {
	    return false;
	}
	if (!CoverLib.checkPlacement(tileWiring.CoverSides, tileWiring.Covers, n6, (tileWiring.ConSides & 64) > 0)) {
	    return false;
	}
	tileWiring.ConSides = n3;
	tileWiring.uncache();
	this.blockUsed(world, worldCoord, itemStack);
	return true;
    }

    @Override
    public String getMicroName(int n, int n2) {
	switch (n) {
	default: {
	    break;
	}
	case 1: {
	    if (n2 != 0)
		break;
	    return "tile.rpwire";
	}
	case 2: {
	    return "tile.rpinsulated." + CoreLib.rawColorNames[n2];
	}
	case 3: {
	    if (n2 == 0) {
		return "tile.rpcable";
	    }
	    return "tile.rpcable." + CoreLib.rawColorNames[n2 - 1];
	}
	case 5: {
	    if (n2 != 0)
		break;
	    return "tile.bluewire";
	}
	}
	return null;
    }

    @Override
    public void addCreativeItems(int n, ArrayList arrayList) {
	switch (n) {
	default: {
	    break;
	}
	case 1: {
	    arrayList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 256));
	    break;
	}
	case 2: {
	    for (int i = 0; i < 16; ++i) {
		arrayList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 512 + i));
	    }
	    break;
	}
	case 3: {
	    for (int i = 0; i < 17; ++i) {
		arrayList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 768 + i));
	    }
	    break;
	}
	case 5: {
	    arrayList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 1280));
	}
	}
    }
}
