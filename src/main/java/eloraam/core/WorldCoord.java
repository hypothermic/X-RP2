/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.TileEntity;

import java.util.Comparator;

public class WorldCoord implements Comparable {

    public int x;
    public int y;
    public int z;

    public WorldCoord(int n, int n2, int n3) {
        this.x = n;
        this.y = n2;
        this.z = n3;
    }

    public WorldCoord(TileEntity tileEntity) {
        this.x = tileEntity.x;
        this.y = tileEntity.y;
        this.z = tileEntity.z;
    }

    public WorldCoord copy() {
        return new WorldCoord(this.x, this.y, this.z);
    }

    public WorldCoord coordStep(int n) {
        switch (n) {
            case 0: {
                return new WorldCoord(this.x, this.y - 1, this.z);
            }
            case 1: {
                return new WorldCoord(this.x, this.y + 1, this.z);
            }
            case 2: {
                return new WorldCoord(this.x, this.y, this.z - 1);
            }
            case 3: {
                return new WorldCoord(this.x, this.y, this.z + 1);
            }
            case 4: {
                return new WorldCoord(this.x - 1, this.y, this.z);
            }
        }
        return new WorldCoord(this.x + 1, this.y, this.z);
    }

    public void set(WorldCoord worldCoord) {
        this.x = worldCoord.x;
        this.y = worldCoord.y;
        this.z = worldCoord.z;
    }

    public int squareDist(int n, int n2, int n3) {
        return (n - this.x) * (n - this.x) + (n2 - this.y) * (n2 - this.y) + (n3 - this.z) * (n3 - this.z);
    }

    public void step(int n) {
        switch (n) {
            case 0: {
                --this.y;
                break;
            }
            case 1: {
                ++this.y;
                break;
            }
            case 2: {
                --this.z;
                break;
            }
            case 3: {
                ++this.z;
                break;
            }
            case 4: {
                --this.x;
                break;
            }
            default: {
                ++this.x;
            }
        }
    }

    public void indStep(int n, int n2) {
        int n3;
        this.step(n);
        switch (n) {
            case 0: {
                n3 = n2 + 2;
                break;
            }
            case 1: {
                n3 = n2 + 2;
                break;
            }
            case 2: {
                n3 = n2 + (n2 & 2);
                break;
            }
            case 3: {
                n3 = n2 + (n2 & 2);
                break;
            }
            case 4: {
                n3 = n2;
                break;
            }
            default: {
                n3 = n2;
            }
        }
        this.step(n3);
    }

    public int hashCode() {
        int n = Integer.valueOf(this.x).hashCode();
        int n2 = Integer.valueOf(this.y).hashCode();
        int n3 = Integer.valueOf(this.z).hashCode();
        return n + 31 * (n2 + 31 * n3);
    }

    public int compareTo(Object object) {
        WorldCoord worldCoord = (WorldCoord) object;
        if (this.x == worldCoord.x) {
            if (this.y == worldCoord.y) {
                return this.z - worldCoord.z;
            }
            return this.y - worldCoord.y;
        }
        return this.x - worldCoord.x;
    }

    public boolean equals(Object object) {
        if (!(object instanceof WorldCoord)) {
            return false;
        }
        WorldCoord worldCoord = (WorldCoord) object;
        return this.x == worldCoord.x && this.y == worldCoord.y && this.z == worldCoord.z;
    }

    public static Comparator getCompareDir(int n) {
        return new WCComparator(n);
    }

    public static class WCComparator implements Comparator {

        int dir;

        public int compare(Object object, Object object2) {
            WorldCoord worldCoord = (WorldCoord) object;
            WorldCoord worldCoord2 = (WorldCoord) object2;
            switch (this.dir) {
                case 0: {
                    return worldCoord.y - worldCoord2.y;
                }
                case 1: {
                    return worldCoord2.y - worldCoord.y;
                }
                case 2: {
                    return worldCoord.z - worldCoord2.z;
                }
                case 3: {
                    return worldCoord2.z - worldCoord.z;
                }
                case 4: {
                    return worldCoord.x - worldCoord2.x;
                }
            }
            return worldCoord2.x - worldCoord.x;
        }

        private WCComparator(int n) {
            this.dir = n;
        }
    }

}
