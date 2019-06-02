/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

public class CoreLib {

    public static Comparator itemStackComparator = new Comparator() {

        public int compare(ItemStack itemStack, ItemStack itemStack2) {
            return CoreLib.compareItemStack(itemStack, itemStack2);
        }

        public int compare(Object object, Object object2) {
            return this.compare((ItemStack) object, (ItemStack) object2);
        }
    };
    public static String[] rawColorNames = new String[]{"white", "orange", "magenta", "lightBlue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"};
    public static String[] enColorNames = new String[]{"White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};
    public static int[] paintColors = new int[]{16777215, 16744448, 16711935, 7110911, 16776960, 65280, 16737408, 5460819, 9671571, 65535, 8388863, 255, 5187328, 32768, 16711680, 2039583};
    public static final Material materialRedpower = new Material(MaterialMapColor.o);

    void initModule(String string) {
        Method method;
        Class class_;
        try {
            class_ = Class.forName(string);
        } catch (ClassNotFoundException classNotFoundException) {
            return;
        }
        try {
            method = class_.getDeclaredMethod("initialize", new Class[0]);
        } catch (NoSuchMethodException noSuchMethodException) {
            return;
        }
        try {
            method.invoke(null, new Object[0]);
        } catch (IllegalAccessException illegalAccessException) {
            return;
        } catch (InvocationTargetException invocationTargetException) {
            return;
        }
    }

    public static Object getTileEntity(IBlockAccess iBlockAccess, int n, int n2, int n3, Class class_) {
        TileEntity tileEntity = iBlockAccess.getTileEntity(n, n2, n3);
        if (!class_.isInstance((Object) tileEntity)) {
            return null;
        }
        return tileEntity;
    }

    public static Object getTileEntity(IBlockAccess iBlockAccess, WorldCoord worldCoord, Class class_) {
        TileEntity tileEntity = iBlockAccess.getTileEntity(worldCoord.x, worldCoord.y, worldCoord.z);
        if (!class_.isInstance((Object) tileEntity)) {
            return null;
        }
        return tileEntity;
    }

    public static Object getGuiTileEntity(World world, int n, int n2, int n3, Class class_) {
        if (world.isStatic) {
            try {
                return class_.newInstance();
            } catch (InstantiationException instantiationException) {
                return null;
            } catch (IllegalAccessException illegalAccessException) {
                return null;
            }
        }
        TileEntity tileEntity = world.getTileEntity(n, n2, n3);
        if (!class_.isInstance((Object) tileEntity)) {
            return null;
        }
        return tileEntity;
    }

    public static void markBlockDirty(World world, int n, int n2, int n3) {
        if (world.isLoaded(n, n2, n3)) {
            world.getChunkAtWorldCoords(n, n3).e();
        }
    }

    public static int compareItemStack(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack.id != itemStack2.id) {
            return itemStack.id - itemStack2.id;
        }
        if (itemStack.getData() == itemStack2.getData()) {
            return 0;
        }
        if (itemStack.getItem().e()) {
            return itemStack.getData() - itemStack2.getData();
        }
        return 0;
    }

    public static void dropItem(World world, int n, int n2, int n3, ItemStack itemStack) {
        if (CoreProxy.isClient(world)) {
            return;
        }
        double d = 0.7;
        double d2 = (double) world.random.nextFloat() * d + (1.0 - d) * 0.5;
        double d3 = (double) world.random.nextFloat() * d + (1.0 - d) * 0.5;
        double d4 = (double) world.random.nextFloat() * d + (1.0 - d) * 0.5;
        EntityItem entityItem = new EntityItem(world, (double) n + d2, (double) n2 + d3, (double) n3 + d4, itemStack);
        entityItem.pickupDelay = 10;
        world.addEntity((Entity) entityItem);
    }

    public static ItemStack copyStack(ItemStack itemStack, int n) {
        return new ItemStack(itemStack.id, n, itemStack.getData());
    }

    public static int rotToSide(int n) {
        switch (n) {
            case 0: {
                return 5;
            }
            case 1: {
                return 3;
            }
            case 2: {
                return 4;
            }
        }
        return 2;
    }

    public static MovingObjectPosition retraceBlock(World world, EntityHuman entityHuman, int n, int n2, int n3) {
        Vec3D vec3D = Vec3D.create((double) entityHuman.locX, (double) (entityHuman.locY + 1.62 - (double) entityHuman.height), (double) entityHuman.locZ);
        Vec3D vec3D2 = entityHuman.f(1.0f);
        Vec3D vec3D3 = vec3D.add(vec3D2.a * 5.0, vec3D2.b * 5.0, vec3D2.c * 5.0);
        Block block = Block.byId[world.getTypeId(n, n2, n3)];
        if (block == null) {
            return null;
        }
        return block.a(world, n, n2, n3, vec3D, vec3D3);
    }

    public static MovingObjectPosition traceBlock(EntityHuman entityHuman) {
        Vec3D vec3D = Vec3D.create((double) entityHuman.locX, (double) (entityHuman.locY + 1.62 - (double) entityHuman.height), (double) entityHuman.locZ);
        Vec3D vec3D2 = entityHuman.f(1.0f);
        Vec3D vec3D3 = vec3D.add(vec3D2.a * 5.0, vec3D2.b * 5.0, vec3D2.c * 5.0);
        return entityHuman.world.a(vec3D, vec3D3);
    }

    public static void placeNoise(World world, int n, int n2, int n3, int n4) {
        Block block = Block.byId[n4];
        world.makeSound((double) ((float) n + 0.5f), (double) ((float) n2 + 0.5f), (double) ((float) n3 + 0.5f), "step.stone", (block.stepSound.getVolume1() + 1.0f) / 2.0f, block.stepSound.getVolume2() * 0.8f);
    }

    public static int getBurnTime(ItemStack itemStack) {
        return TileEntityFurnace.fuelTime((ItemStack) itemStack);
    }

    public static double getAverageEdgeLength(AxisAlignedBB axisAlignedBB) {
        double d = axisAlignedBB.d - axisAlignedBB.a;
        double d2 = axisAlignedBB.e - axisAlignedBB.b;
        double d3 = axisAlignedBB.f - axisAlignedBB.c;
        return (d + d2 + d3) / 3.0;
    }

}
