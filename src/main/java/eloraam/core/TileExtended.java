/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.*;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class TileExtended extends TileEntity {

    protected long timeSched = -1;
    public static LinkedBlockingQueue<TileExtended> tileExtQueue = new LinkedBlockingQueue();

    static {
        new Thread() {

            @Override
            public void run() {
                Thread.currentThread().setName("EE2-Mk3Thread");
                do {
                    try {
                        do {
                            if (!TileExtended.tileExtQueue.isEmpty()) {
                                TileExtended obj = TileExtended.tileExtQueue.poll();
                                obj.processUpdate();
                                continue;
                            }
                            Thread.sleep(50);
                        } while (true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                } while (true);
            }
        }.start();
    }

    public void a(NBTTagCompound arg0) {
        super.a(arg0);
        this.timeSched = arg0.getLong("sched");
    }

    public void b(NBTTagCompound arg0) {
        super.b(arg0);
        arg0.setLong("sched", this.timeSched);
    }

    public void breakBlock() {
        CoreLib.dropItem(this.world, this.x, this.y, this.z, new ItemStack(this.getBlockID(), 1, this.getExtendedID()));
        this.world.setTypeId(this.x, this.y, this.z, 0);
    }

    public void dirtyBlock() {
        CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
    }

    public abstract int getBlockID();

    public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    public int getExtendedID() {
        return 0;
    }

    public int getExtendedMetadata() {
        return 0;
    }

    public boolean isBlockStrongPoweringTo(int arg0) {
        return false;
    }

    public boolean isBlockWeakPoweringTo(int arg0) {
        return this.isBlockStrongPoweringTo(arg0);
    }

    public boolean isTickRunnable() {
        if (this.timeSched >= 0 && this.timeSched <= this.world.getTime()) {
            return true;
        }
        return false;
    }

    public boolean isTickScheduled() {
        if (this.timeSched >= 0) {
            return true;
        }
        return false;
    }

    public boolean onBlockActivated(EntityHuman arg0) {
        return false;
    }

    public void onBlockNeighborChange(int arg0) {
    }

    public void onBlockPlacedBy(EntityLiving arg0) {
    }

    public void onBlockRemoval() {
    }

    public void onEntityCollidedWithBlock(Entity arg0) {
    }

    public void onTileTick() {
    }

    public void q_() {
        this.processUpdate();
    }

    public void processUpdate() {
        try {
            if (!CoreProxy.isClient(this.world) && this.timeSched >= 0) {
                long arg0 = this.world.getTime();
                if (this.timeSched > arg0 + 1200) {
                    this.timeSched = arg0 + 1200;
                } else if (this.timeSched <= arg0) {
                    this.timeSched = -1;
                    this.onTileTick();
                    this.dirtyBlock();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scheduleTick(int arg0) {
        long arg1 = this.world.getTime() + (long) arg0;
        if (this.timeSched <= 0 || this.timeSched >= arg1) {
            this.timeSched = arg1;
            this.dirtyBlock();
        }
    }

    public void setExtendedMetadata(int arg0) {
    }

    public void updateBlock() {
        int arg0 = this.world.getData(this.x, this.y, this.z);
        this.world.notify(this.x, this.y, this.z);
        CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
    }

    public void updateBlockChange() {
        RedPowerLib.updateIndirectNeighbors(this.world, this.x, this.y, this.z, this.getBlockID());
        this.world.notify(this.x, this.y, this.z);
        CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
    }

}
