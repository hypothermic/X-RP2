/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.EntityLiving
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.*;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class TileGrate
        extends TileMachinePanel
        implements IPipeConnectable {
    FluidBuffer gratebuf;
    GratePathfinder searchPath;
    int searchState;

    public TileGrate() {
        this.gratebuf = new FluidBuffer() {

            @Override
            public TileEntity getParent() {
                return TileGrate.this;
            }

            @Override
            public void onChange() {
                TileGrate.this.dirtyBlock();
            }
        };
        this.searchPath = null;
        this.searchState = 0;
    }

    @Override
    public int getPartMaxRotation(int n, boolean bl) {
        return !bl ? 5 : 0;
    }

    @Override
    public int getPipeConnectableSides() {
        return 1 << this.Rotation;
    }

    @Override
    public int getPipeFlangeSides() {
        return 1 << this.Rotation;
    }

    @Override
    public int getPipePressure(int n) {
        return 0;
    }

    @Override
    public FluidBuffer getPipeBuffer(int n) {
        return this.gratebuf;
    }

    @Override
    public void onFramePickup(IBlockAccess iBlockAccess) {
        this.restartPath();
    }

    @Override
    public int getExtendedID() {
        return 3;
    }

    @Override
    public void onBlockPlacedBy(EntityLiving entityLiving) {
        this.Rotation = this.getFacing(entityLiving);
        this.updateBlockChange();
    }

    @Override
    public void onBlockNeighborChange(int n) {
    }

    @Override
    public void q_() {
        super.q_();
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (!this.isTickScheduled()) {
            this.scheduleTick(5);
        }
        if (this.searchState == 1) {
            this.searchPath.tryMapFluid(400);
        }
        PipeLib.movePipeLiquid(this.world, this, new WorldCoord(this), 1 << this.Rotation);
    }

    public void restartPath() {
        this.searchPath = null;
        this.searchState = 0;
    }

    @Override
    public void onTileTick() {
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        WorldCoord worldCoord = new WorldCoord(this);
        worldCoord.step(this.Rotation);
        IPipeConnectable iPipeConnectable = (IPipeConnectable) CoreLib.getTileEntity((IBlockAccess) this.world, worldCoord, IPipeConnectable.class);
        if (iPipeConnectable == null) {
            return;
        }
        int n = iPipeConnectable.getPipePressure(this.Rotation ^ 1);
        if (n == 0) {
            this.restartPath();
        } else if (n < 0) {
            if (this.gratebuf.getLevel() >= this.gratebuf.getMaxLevel()) {
                return;
            }
            if (this.searchState == 2) {
                this.restartPath();
            }
            if (this.searchState == 0) {
                this.searchState = 1;
                this.searchPath = new GratePathfinder(false);
                if (this.gratebuf.Type == 0) {
                    if (!this.searchPath.startSuck(new WorldCoord(this), 63 ^ 1 << this.Rotation)) {
                        this.restartPath();
                        return;
                    }
                } else {
                    this.searchPath.start(new WorldCoord(this), this.gratebuf.Type, 63 ^ 1 << this.Rotation);
                }
            }
            if (this.searchState == 1) {
                if (!this.searchPath.tryMapFluid(400)) {
                    return;
                }
                int n2 = this.searchPath.trySuckFluid(this.searchPath.fluidClass.getFluidQuanta());
                if (n2 == 0) {
                    return;
                }
                this.gratebuf.addLevel(this.searchPath.fluidID, n2);
            }
        } else if (n > 0) {
            int n3;
            if (this.gratebuf.getLevel() < 16) {
                return;
            }
            if (this.gratebuf.Type == 0) {
                return;
            }
            if (this.searchState == 1) {
                this.restartPath();
            }
            if (this.searchState == 0) {
                this.searchState = 2;
                this.searchPath = new GratePathfinder(true);
                this.searchPath.start(new WorldCoord(this), this.gratebuf.Type, 63 ^ 1 << this.Rotation);
            }
            if (this.searchState == 2 && (n3 = this.searchPath.tryDumpFluid(16, 2000)) != 16) {
                this.gratebuf.addLevel(this.gratebuf.Type, n3 - 16);
            }
        }
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        this.gratebuf.readFromNBT(nBTTagCompound, "buf");
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        this.gratebuf.writeToNBT(nBTTagCompound, "buf");
    }

    public static class SimpleComparator
            implements Comparator {
        int dir;

        public int compare(Object object, Object object2) {
            FluidCoord fluidCoord = (FluidCoord) object;
            FluidCoord fluidCoord2 = (FluidCoord) object2;
            return fluidCoord.dist - fluidCoord2.dist;
        }
    }

    public static class FluidCoord
            implements Comparable {
        public WorldCoord wc;
        public int dist;

        public int compareTo(Object object) {
            FluidCoord fluidCoord = (FluidCoord) object;
            if (this.wc.y == fluidCoord.wc.y) {
                return this.dist - fluidCoord.dist;
            }
            return this.wc.y - fluidCoord.wc.y;
        }

        public FluidCoord(WorldCoord worldCoord, int n) {
            this.wc = worldCoord;
            this.dist = n;
        }
    }

    public class GratePathfinder {
        WorldCoord startPos;
        HashMap backlink;
        PriorityQueue workset;
        PriorityQueue allset;
        public int fluidID;
        public FluidClass fluidClass;

        public void start(WorldCoord worldCoord, int n, int n2) {
            this.fluidID = n;
            this.fluidClass = PipeLib.getLiquidClass(this.fluidID);
            this.startPos = worldCoord;
            for (int i = 0; i < 6; ++i) {
                if ((n2 & 1 << i) == 0) continue;
                WorldCoord worldCoord2 = worldCoord.coordStep(i);
                this.backlink.put(worldCoord2, worldCoord);
                this.workset.add(new FluidCoord(worldCoord2, 0));
            }
        }

        public boolean startSuck(WorldCoord worldCoord, int n) {
            this.fluidID = 0;
            this.startPos = worldCoord;
            for (int i = 0; i < 6; ++i) {
                if ((n & 1 << i) == 0) continue;
                WorldCoord worldCoord2 = worldCoord.coordStep(i);
                this.backlink.put(worldCoord2, worldCoord);
                this.workset.add(new FluidCoord(worldCoord2, 0));
                int n2 = PipeLib.getLiquidId(TileGrate.this.world, worldCoord2);
                if (n2 == 0) continue;
                this.fluidID = n2;
            }
            if (this.fluidID == 0) {
                return false;
            }
            this.fluidClass = PipeLib.getLiquidClass(this.fluidID);
            return true;
        }

        public boolean isConnected(WorldCoord worldCoord) {
            if (worldCoord.compareTo(this.startPos) == 0) {
                return true;
            }
            do {
                if ((worldCoord = (WorldCoord) this.backlink.get(worldCoord)) == null) {
                    return false;
                }
                if (worldCoord.compareTo(this.startPos) != 0) continue;
                return true;
            } while (this.fluidClass.getFluidId(TileGrate.this.world, worldCoord) == this.fluidID);
            return false;
        }

        public void stepAdd(FluidCoord fluidCoord) {
            for (int i = 0; i < 6; ++i) {
                WorldCoord worldCoord = fluidCoord.wc.coordStep(i);
                if (this.backlink.containsKey(worldCoord)) continue;
                this.backlink.put(worldCoord, fluidCoord.wc);
                this.workset.add(new FluidCoord(worldCoord, fluidCoord.dist + 1));
            }
        }

        public void stepMap(FluidCoord fluidCoord) {
            for (int i = 0; i < 6; ++i) {
                WorldCoord worldCoord = fluidCoord.wc.coordStep(i);
                if (this.fluidClass.getFluidId(TileGrate.this.world, worldCoord) != this.fluidID || this.backlink.containsKey(worldCoord))
                    continue;
                this.backlink.put(worldCoord, fluidCoord.wc);
                this.workset.add(new FluidCoord(worldCoord, fluidCoord.dist + 1));
            }
        }

        public int tryDumpFluid(int n, int n2) {
            for (int i = 0; i < n2; ++i) {
                int n3;
                FluidCoord fluidCoord = (FluidCoord) this.workset.poll();
                if (fluidCoord == null) {
                    TileGrate.this.restartPath();
                    return n;
                }
                if (!this.isConnected(fluidCoord.wc)) {
                    TileGrate.this.restartPath();
                    return n;
                }
                if (TileGrate.this.world.getTypeId(fluidCoord.wc.x, fluidCoord.wc.y, fluidCoord.wc.z) == 0) {
                    if (!this.fluidClass.setFluidLevel(TileGrate.this.world, fluidCoord.wc, n)) continue;
                    this.stepAdd(fluidCoord);
                    return 0;
                }
                if (this.fluidClass.getFluidId(TileGrate.this.world, fluidCoord.wc) != this.fluidID) continue;
                this.stepAdd(fluidCoord);
                int n4 = this.fluidClass.getFluidLevel(TileGrate.this.world, fluidCoord.wc);
                if (n4 == 16 || !this.fluidClass.setFluidLevel(TileGrate.this.world, fluidCoord.wc, n3 = Math.min(n4 + n, 16)) || (n -= n3 - n4) != 0)
                    continue;
                return 0;
            }
            return n;
        }

        public boolean tryMapFluid(int n) {
            if (this.allset.size() > 32768) {
                return true;
            }
            for (int i = 0; i < n; ++i) {
                FluidCoord fluidCoord = (FluidCoord) this.workset.poll();
                if (fluidCoord == null) {
                    return true;
                }
                if (this.fluidClass.getFluidId(TileGrate.this.world, fluidCoord.wc) != this.fluidID) continue;
                this.stepMap(fluidCoord);
                int n2 = this.fluidClass.getFluidLevel(TileGrate.this.world, fluidCoord.wc);
                if (n2 <= 0) continue;
                this.allset.add(fluidCoord);
            }
            return false;
        }

        public int trySuckFluid(int n) {
            int n2 = 0;
            while (!this.allset.isEmpty()) {
                FluidCoord fluidCoord = (FluidCoord) this.allset.peek();
                if (!this.isConnected(fluidCoord.wc)) {
                    TileGrate.this.restartPath();
                    return n2;
                }
                if (this.fluidClass.getFluidId(TileGrate.this.world, fluidCoord.wc) != this.fluidID) {
                    this.allset.poll();
                    continue;
                }
                int n3 = this.fluidClass.getFluidLevel(TileGrate.this.world, fluidCoord.wc);
                if (n3 == 0) {
                    this.allset.poll();
                    continue;
                }
                if (n2 + n3 <= n) {
                    TileGrate.this.world.setTypeId(fluidCoord.wc.x, fluidCoord.wc.y, fluidCoord.wc.z, 0);
                    this.allset.poll();
                    if ((n2 += n3) == n) {
                        return n;
                    }
                }
                if (!this.fluidClass.setFluidLevel(TileGrate.this.world, fluidCoord.wc, n - n2)) continue;
                return n;
            }
            TileGrate.this.restartPath();
            return n2;
        }

        public GratePathfinder(boolean bl) {
            this.backlink = new HashMap();
            this.allset = new PriorityQueue(1024, Collections.reverseOrder());
            this.workset = bl ? new PriorityQueue() : new PriorityQueue(1024, new SimpleComparator());
        }
    }

}

