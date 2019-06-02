/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 *  org.bukkit.block.Block
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.plugin.PluginManager
 */
package eloraam.machine;

import eloraam.core.*;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.RedPowerMachine;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

public class TileBreaker
        extends TileMachine
        implements ITubeConnectable,
        IFrameLink,
        IConnectable {
    TubeBuffer buffer;

    public TileBreaker() {
        this.buffer = new TubeBuffer(this);
    }

    @Override
    public boolean isFrameMoving() {
        return false;
    }

    @Override
    public boolean canFrameConnectIn(int n) {
        return n != (this.Rotation ^ 1);
    }

    @Override
    public boolean canFrameConnectOut(int n) {
        return false;
    }

    @Override
    public WorldCoord getFrameLinkset() {
        return null;
    }

    @Override
    public int getConnectableMask() {
        return 1073741823 ^ RedPowerLib.getConDirMask(this.Rotation ^ 1);
    }

    @Override
    public int getConnectClass(int n) {
        return 0;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public int getTubeConnectableSides() {
        return 1 << this.Rotation;
    }

    @Override
    public int getTubeConClass() {
        return 0;
    }

    @Override
    public boolean canRouteItems() {
        return false;
    }

    @Override
    public boolean tubeItemEnter(int n, int n2, TubeItem tubeItem) {
        if (n == this.Rotation && n2 == 2) {
            this.buffer.addBounce(tubeItem);
            this.Active = true;
            this.scheduleTick(5);
            return true;
        }
        return false;
    }

    @Override
    public boolean tubeItemCanEnter(int n, int n2, TubeItem tubeItem) {
        return n == this.Rotation && n2 == 2;
    }

    @Override
    public int tubeWeight(int n, int n2) {
        if (n == this.Rotation && n2 == 2) {
            return this.buffer.size();
        }
        return 0;
    }

    @Override
    public void onBlockNeighborChange(int n) {
        int n2 = this.getConnectableMask();
        if (RedPowerLib.isPowered((IBlockAccess) this.world, this.x, this.y, this.z, n2, n2 >> 24)) {
            if (this.Powered) {
                return;
            }
        } else {
            if (this.Active && !this.isTickScheduled()) {
                this.scheduleTick(5);
            }
            if (!this.Powered) {
                return;
            }
            this.Powered = false;
            return;
        }
        this.Powered = true;
        this.dirtyBlock();
        if (this.Active) {
            return;
        }
        WorldCoord worldCoord = new WorldCoord(this.x, this.y, this.z);
        worldCoord.step(this.Rotation ^ 1);
        int n3 = this.world.getTypeId(worldCoord.x, worldCoord.y, worldCoord.z);
        if (n3 == 0 || n3 == net.minecraft.server.Block.BEDROCK.id) {
            return;
        }
        int n4 = this.world.getData(worldCoord.x, worldCoord.y, worldCoord.z);
        if (RedPowerMachine.breakerBlacklist.contains(n4 << 15 | n3) || RedPowerMachine.breakerBlacklist.contains(-32768 | n3)) {
            return;
        }
        Block block = this.world.getWorld().getBlockAt(worldCoord.x, worldCoord.y, worldCoord.z);
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, (Player) FakePlayer.getBukkitEntity(this.world));
        this.world.getServer().getPluginManager().callEvent((Event) blockBreakEvent);
        if (blockBreakEvent.isCancelled()) {
            return;
        }
        this.Active = true;
        this.updateBlock();
        net.minecraft.server.Block block2 = net.minecraft.server.Block.byId[n3];
        int n5 = this.world.getData(worldCoord.x, worldCoord.y, worldCoord.z);
        this.buffer.addAll(block2.getBlockDropped(this.world, worldCoord.x, worldCoord.y, worldCoord.z, n5, 0));
        this.world.setTypeId(worldCoord.x, worldCoord.y, worldCoord.z, 0);
        this.drainBuffer();
        if (!this.buffer.isEmpty()) {
            this.scheduleTick(5);
        }
    }

    public void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem tubeItem = this.buffer.getLast();
            if (!this.handleItem(tubeItem)) {
                this.buffer.plugged = true;
                return;
            }
            this.buffer.pop();
            if (!this.buffer.plugged) continue;
            return;
        }
    }

    @Override
    public void onBlockRemoval() {
        this.buffer.onRemove(this);
    }

    @Override
    public void onTileTick() {
        if (!this.buffer.isEmpty()) {
            this.drainBuffer();
            if (!this.buffer.isEmpty()) {
                this.scheduleTick(10);
            } else {
                this.scheduleTick(5);
            }
            return;
        }
        if (!this.Powered) {
            this.Active = false;
            this.updateBlock();
        }
    }

    @Override
    public int getExtendedID() {
        return 1;
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        this.buffer.readFromNBT(nBTTagCompound);
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        this.buffer.writeToNBT(nBTTagCompound);
    }
}

