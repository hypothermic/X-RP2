/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.Block;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.World;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

public class FrameLib {

    public static class FrameSolver {

        HashSet scanmap;
        LinkedList scanpos;
        HashSet framemap;
        LinkedList frameset;
        LinkedList clearset;
        int movedir;
        WorldCoord motorpos;
        public boolean valid = true;
        World worldObj;

        boolean step() {
            WorldCoord worldCoord = (WorldCoord) this.scanpos.removeFirst();
            if (worldCoord.y < 0 || worldCoord.y >= this.worldObj.getHeight() - 1) {
                return false;
            }
            int n = this.worldObj.getTypeId(worldCoord.x, worldCoord.y, worldCoord.z);
            if (this.movedir >= 0 && !this.worldObj.isLoaded(worldCoord.x, worldCoord.y, worldCoord.z)) {
                this.valid = false;
                return false;
            }
            if (n == 0) {
                return false;
            }
            int n2 = this.worldObj.getData(worldCoord.x, worldCoord.y, worldCoord.z);
            if (this.movedir >= 0 && Block.byId[n].getHardness(n2) < 0.0f) {
                this.valid = false;
                return false;
            }
            this.framemap.add(worldCoord);
            this.frameset.addLast(worldCoord);
            IFrameLink iFrameLink = (IFrameLink) CoreLib.getTileEntity((IBlockAccess) this.worldObj, worldCoord, IFrameLink.class);
            if (iFrameLink == null) {
                return true;
            }
            if (iFrameLink.isFrameMoving() && this.movedir >= 0) {
                this.valid = false;
                return true;
            }
            for (int i = 0; i < 6; ++i) {
                WorldCoord worldCoord2;
                IFrameLink iFrameLink2;
                WorldCoord worldCoord3;
                if (!iFrameLink.canFrameConnectOut(i) || this.scanmap.contains(worldCoord2 = worldCoord.coordStep(i)) || (iFrameLink2 = (IFrameLink) CoreLib.getTileEntity((IBlockAccess) this.worldObj, worldCoord2, IFrameLink.class)) != null && (!iFrameLink2.canFrameConnectIn((i ^ 1) & 255) || this.movedir < 0 && ((worldCoord3 = iFrameLink2.getFrameLinkset()) == null || !worldCoord3.equals(this.motorpos))))
                    continue;
                this.scanmap.add(worldCoord2);
                this.scanpos.addLast(worldCoord2);
            }
            return true;
        }

        public boolean solve() {
            while (this.valid && this.scanpos.size() > 0) {
                this.step();
            }
            return this.valid;
        }

        public boolean solveLimit(int n) {
            while (this.valid && this.scanpos.size() > 0) {
                if (this.step()) {
                    --n;
                }
                if (n != 0)
                    continue;
                return false;
            }
            return this.valid;
        }

        public boolean addMoved() {
            LinkedList<WorldCoord> linkedList = (LinkedList) this.frameset.clone();
            for (WorldCoord worldCoord : linkedList) {
                WorldCoord worldCoord2 = worldCoord.coordStep(this.movedir);
                int n = this.worldObj.getTypeId(worldCoord2.x, worldCoord2.y, worldCoord2.z);
                if (!this.worldObj.isLoaded(worldCoord2.x, worldCoord2.y, worldCoord2.z)) {
                    this.valid = false;
                    return false;
                }
                if (this.framemap.contains(worldCoord2))
                    continue;
                if (n != 0) {
                    if (!this.worldObj.mayPlace(Block.STONE.id, worldCoord2.x, worldCoord2.y, worldCoord2.z, true, 0)) {
                        this.valid = false;
                        return false;
                    }
                    this.clearset.add(worldCoord2);
                }
                this.framemap.add(worldCoord2);
                this.frameset.addLast(worldCoord2);
            }
            return this.valid;
        }

        public void sort(int n) {
            Collections.sort(this.frameset, WorldCoord.getCompareDir(n));
        }

        public LinkedList<WorldCoord> getFrameSet() { // X-RP: type decl
            return this.frameset;
        }

        public LinkedList<WorldCoord> getClearSet() { // X-RP: type decl
            return this.clearset;
        }

        public FrameSolver(World world, WorldCoord worldCoord, WorldCoord worldCoord2, int n) {
            this.movedir = n;
            this.motorpos = worldCoord2;
            this.worldObj = world;
            this.scanmap = new HashSet();
            this.scanpos = new LinkedList();
            this.framemap = new HashSet();
            this.frameset = new LinkedList();
            this.clearset = new LinkedList();
            this.scanmap.add(worldCoord2);
            this.scanmap.add(worldCoord);
            this.scanpos.addLast(worldCoord);
        }
    }
}
