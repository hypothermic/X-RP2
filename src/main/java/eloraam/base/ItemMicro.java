/* X-RP - decompiled with CFR */
// NOTE: ItemMicro had some issues with decompiling, see comments in code.
package eloraam.base;

import eloraam.base.BlockMicro;
import eloraam.core.CoreLib;
import eloraam.core.CoverLib;
import eloraam.core.ICoverable;
import eloraam.core.IMicroPlacement;
import eloraam.core.RedPowerLib;
import eloraam.core.WorldCoord;
import java.util.ArrayList;
import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EnumMovingObjectType;
import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.RedPowerBase;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockPlaceEvent;

public class ItemMicro extends ItemBlock {

    IMicroPlacement[] placers = new IMicroPlacement[256];

    public ItemMicro(int n) {
	super(n);
	this.setMaxDurability(0);
	this.a(true);
	this.setNoRepair();
    }

    public int filterData(int n) {
	return n;
    }

    private boolean useCover(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
	Object craftBlockState; // X-RP: change from CraftBlockState to Object due to conflict at line 69 (starts with "if (!((".)
	ICoverable iCoverable;
	MovingObjectPosition movingObjectPosition = CoreLib.retraceBlock(world, entityHuman, n, n2, n3);
	if (movingObjectPosition == null) {
	    return false;
	}
	if (movingObjectPosition.type != EnumMovingObjectType.TILE) {
	    return false;
	}
	if ((movingObjectPosition = CoverLib.getPlacement(world, movingObjectPosition, itemStack.getData())) == null) {
	    return false;
	}
	if (world.mayPlace(itemStack.id, movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d, false, n4)) {
	    craftBlockState = CraftBlockState.getBlockState((World) world, (int) movingObjectPosition.b, (int) movingObjectPosition.c, (int) movingObjectPosition.d);
	    if (world.setRawTypeIdAndData(movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d, RedPowerBase.blockMicro.id, 0)) {
		// X-RP: beginning of wonky cb stuff
		iCoverable = (ICoverable) CraftEventFactory.callBlockPlaceEvent((World) world, (EntityHuman) entityHuman, (BlockState) craftBlockState, (int) n, (int) n2, (int) n3);
		if (((BlockPlaceEvent) iCoverable).isCancelled() || !((BlockPlaceEvent) iCoverable).canBuild()) {
		    world.setTypeIdAndData(movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d, ((CraftBlockState) craftBlockState).getTypeId(), (int) ((CraftBlockState) craftBlockState).getRawData());
		    return false;
		}
		// X-RP: end of wonky cb stuff
		world.notify(movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d);
		world.applyPhysics(movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d, RedPowerBase.blockMicro.id);
	    }
	}
	if (!((craftBlockState = world.getTileEntity(movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d)) instanceof ICoverable)) {
	    return false;
	}
	iCoverable = (ICoverable) craftBlockState;
	if (iCoverable.tryAddCover(movingObjectPosition.subHit, CoverLib.damageToCoverValue(itemStack.getData()))) {
	    --itemStack.count;
	    CoreLib.placeNoise(world, movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d, CoverLib.getBlock((int) (itemStack.getData() & 255)).id);
	    RedPowerLib.updateIndirectNeighbors(world, movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d, RedPowerBase.blockMicro.id);
	    world.notify(movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d);
	    return true;
	}
	return false;
    }

    public boolean interactWith(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
	if (entityHuman == null) {
	    return false;
	}
	if (entityHuman.isSneaking()) {
	    return false;
	}
	WorldCoord worldCoord = new WorldCoord(n, n2, n3);
	worldCoord.step(n4);
	CraftBlockState craftBlockState = CraftBlockState.getBlockState((World) world, (int) worldCoord.x, (int) worldCoord.y, (int) worldCoord.z);
	world.suppressPhysics = true;
	world.setTypeIdAndData(worldCoord.x, worldCoord.y, worldCoord.z, itemStack.id, this.filterData(itemStack.getData()));
	BlockPlaceEvent blockPlaceEvent = CraftEventFactory.callBlockPlaceEvent((World) world, (EntityHuman) entityHuman, (BlockState) craftBlockState, (int) n, (int) n2, (int) n3);
	craftBlockState.update(true);
	world.suppressPhysics = false;
	if (blockPlaceEvent.isCancelled() || !blockPlaceEvent.canBuild()) {
	    return true;
	}
	return this.itemUseShared(itemStack, entityHuman, world, n, n2, n3, n4);
    }

    public boolean onItemUseFirst(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
	if (!entityHuman.isSneaking()) {
	    return false;
	}
	WorldCoord worldCoord = new WorldCoord(n, n2, n3);
	worldCoord.step(n4);
	CraftBlockState craftBlockState = CraftBlockState.getBlockState((World) world, (int) worldCoord.x, (int) worldCoord.y, (int) worldCoord.z);
	world.suppressPhysics = true;
	world.setTypeIdAndData(worldCoord.x, worldCoord.y, worldCoord.z, itemStack.id, this.filterData(itemStack.getData()));
	BlockPlaceEvent blockPlaceEvent = CraftEventFactory.callBlockPlaceEvent((World) world, (EntityHuman) entityHuman, (BlockState) craftBlockState, (int) n, (int) n2, (int) n3);
	craftBlockState.update(true);
	world.suppressPhysics = false;
	if (blockPlaceEvent.isCancelled() || !blockPlaceEvent.canBuild()) {
	    return true;
	}
	return this.itemUseShared(itemStack, entityHuman, world, n, n2, n3, n4);
    }

    private boolean itemUseShared(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
	int n5 = itemStack.getData();
	int n6 = n5 & 255;
	if ((n5 >>= 8) == 0 || n5 >= 16 && n5 <= 45) {
	    return this.useCover(itemStack, entityHuman, world, n, n2, n3, n4);
	}
	if (this.placers[n5] == null) {
	    return false;
	}
	return this.placers[n5].onPlaceMicro(itemStack, entityHuman, world, new WorldCoord(n, n2, n3), n4);
    }

    private String getMicroName(int n) {
	switch (n) {
	case 0: {
	    return "rpcover";
	}
	case 16: {
	    return "rppanel";
	}
	case 17: {
	    return "rpslab";
	}
	case 18: {
	    return "rpcovc";
	}
	case 19: {
	    return "rppanc";
	}
	case 20: {
	    return "rpslabc";
	}
	case 21: {
	    return "rpcovs";
	}
	case 22: {
	    return "rppans";
	}
	case 23: {
	    return "rpslabs";
	}
	case 24: {
	    return "rphcover";
	}
	case 25: {
	    return "rphpanel";
	}
	case 26: {
	    return "rphslab";
	}
	case 27: {
	    return "rpcov3";
	}
	case 28: {
	    return "rpcov5";
	}
	case 29: {
	    return "rpcov6";
	}
	case 30: {
	    return "rpcov7";
	}
	case 31: {
	    return "rphcov3";
	}
	case 32: {
	    return "rphcov5";
	}
	case 33: {
	    return "rphcov6";
	}
	case 34: {
	    return "rphcov7";
	}
	case 35: {
	    return "rpcov3c";
	}
	case 36: {
	    return "rpcov5c";
	}
	case 37: {
	    return "rpcov6c";
	}
	case 38: {
	    return "rpcov7c";
	}
	case 39: {
	    return "rpcov3s";
	}
	case 40: {
	    return "rpcov5s";
	}
	case 41: {
	    return "rpcov6s";
	}
	case 42: {
	    return "rpcov7s";
	}
	case 43: {
	    return "rppole1";
	}
	case 44: {
	    return "rppole2";
	}
	case 45: {
	    return "rppole3";
	}
	}
	return null;
    }

    public String a(ItemStack itemStack) {
	int n = itemStack.getData();
	int n2 = n & 255;
	String string = this.getMicroName(n >>= 8);
	if (string != null) {
	    String string2 = CoverLib.getName(n2);
	    if (string2 == null) {
		throw new IndexOutOfBoundsException();
	    }
	    return "tile." + string + "." + string2;
	}
	if (this.placers[n] == null) {
	    throw new IndexOutOfBoundsException();
	}
	String string3 = this.placers[n].getMicroName(n, n2);
	if (string3 == null) {
	    throw new IndexOutOfBoundsException();
	}
	return string3;
    }

    public void registerPlacement(int n, IMicroPlacement iMicroPlacement) {
	this.placers[n] = iMicroPlacement;
    }

    public void addCreativeItems(ArrayList arrayList) {
	for (int i = 0; i < 255; ++i) {
	    if (this.placers[i] == null)
		continue;
	    this.placers[i].addCreativeItems(i, arrayList);
	}
    }
}
