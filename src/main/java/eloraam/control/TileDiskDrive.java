/* X-RP - decompiled with CFR */
// Classified as "wonky method", see comments in code.
package eloraam.control;

import eloraam.base.ItemScrewdriver;
import eloraam.core.*;
import net.minecraft.server.*;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileDiskDrive extends TileExtended implements IRedbusConnectable, IInventory, IHandlePackets, IFrameSupport {

    public int Rotation = 0;
    public boolean hasDisk = false;
    public boolean Active = false;
    private ItemStack[] contents = new ItemStack[1];
    private int accessTime = 0;
    private byte[] databuf = new byte[128];
    private int sector = 0;
    private int cmdreg = 0;
    private int rbaddr = 2;
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();

    public void onOpen(CraftHumanEntity craftHumanEntity) {
        this.transaction.add((HumanEntity) craftHumanEntity);
    }

    public void onClose(CraftHumanEntity craftHumanEntity) {
        this.transaction.remove((Object) craftHumanEntity);
    }

    public List<HumanEntity> getViewers() {
        return this.transaction;
    }

    public void setMaxStackSize(int n) {
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    @Override
    public int rbGetAddr() {
        return this.rbaddr;
    }

    @Override
    public void rbSetAddr(int n) {
        this.rbaddr = n;
    }

    @Override
    public int rbRead(int n) {
        if (n < 128) {
            return this.databuf[n] & 255;
        }
        switch (n) {
            case 128: {
                return this.sector & 255;
            }
            case 129: {
                return this.sector >> 8;
            }
            case 130: {
                return this.cmdreg & 255;
            }
        }
        return 0;
    }

    @Override
    public void rbWrite(int n, int n2) {
        this.dirtyBlock();
        if (n < 128) {
            this.databuf[n] = (byte) n2;
        } else {
            switch (n) {
                case 128: {
                    this.sector = this.sector & 65280 | n2;
                    break;
                }
                case 129: {
                    this.sector = this.sector & 255 | n2 << 8;
                    break;
                }
                case 130: {
                    this.cmdreg = n2;
                }
            }
        }
    }

    @Override
    public int getConnectableMask() {
        return 16777215;
    }

    @Override
    public int getConnectClass(int n) {
        return 66;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public void onBlockPlacedBy(EntityLiving entityLiving) {
        this.Rotation = (int) Math.floor((double) (entityLiving.yaw * 4.0f / 360.0f) + 0.5) + 1 & 3;
    }

    @Override
    public boolean onBlockActivated(EntityHuman entityHuman) {
        if (entityHuman.isSneaking()) {
            if (CoreProxy.isClient(this.world)) {
                return false;
            }
            ItemStack itemStack = entityHuman.inventory.getItemInHand();
            if (itemStack == null) {
                return false;
            }
            if (!(itemStack.getItem() instanceof ItemScrewdriver)) {
                return false;
            }
            entityHuman.openGui((BaseMod) mod_RedPowerControl.instance, 2, this.world, this.x, this.y, this.z);
            return false;
        }
        if (CoreProxy.isClient(this.world)) {
            return true;
        }
        if (!this.hasDisk) {
            return false;
        }
        if (this.contents[0] == null) {
            return false;
        }
        if (this.Active) {
            return false;
        }
        this.ejectDisk();
        return true;
    }

    @Override
    public int getBlockID() {
        return RedPowerControl.blockPeripheral.id;
    }

    @Override
    public int getExtendedID() {
        return 2;
    }

    @Override
    public void onBlockRemoval() {
        for (int i = 0; i < 1; ++i) {
            ItemStack itemStack = this.contents[i];
            if (itemStack == null || itemStack.count <= 0)
                continue;
            CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
        }
    }

    boolean setDisk(ItemStack itemStack) {
        if (this.contents[0] != null) {
            return false;
        }
        this.setItem(0, itemStack);
        return true;
    }

    private NBTTagCompound getDiskTags() {
        NBTTagCompound nBTTagCompound = this.contents[0].tag;
        if (nBTTagCompound == null) {
            this.contents[0].setTag(new NBTTagCompound());
            nBTTagCompound = this.contents[0].tag;
        }
        return nBTTagCompound;
    }

    private File startDisk() {
        if (this.contents[0].getData() > 0) {
            return null;
        }
        final NBTTagCompound diskTags = this.getDiskTags();
        final File saveDir = DiskLib.getSaveDir(this.world);
        if (diskTags.hasKey("serno")) {
            return DiskLib.getDiskFile(saveDir, diskTags.getString("serno"));
        }
        while (true) {
            final String generateSerialNumber = DiskLib.generateSerialNumber(this.world);
            final File diskFile = DiskLib.getDiskFile(saveDir, generateSerialNumber);
            try {
                if (diskFile.createNewFile()) {
                    diskTags.setString("serno", generateSerialNumber);
                    return diskFile;
                }
                continue;
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    private void runCmd1() {
        Arrays.fill(this.databuf, (byte)0);
        String string;
        if (this.contents[0].getData() > 0) {
            string = "System Disk";
        }
        else {
            final NBTTagCompound tag = this.contents[0].tag;
            if (tag == null) {
                return;
            }
            string = tag.getString("label");
        }
        final byte[] bytes = string.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(bytes, 0, this.databuf, 0, Math.min(bytes.length, 128));
    }

    private void runCmd2() {
        if (this.contents[0].getData() > 0) {
            this.cmdreg = -1;
        } else {
            int n;
            NBTTagCompound nBTTagCompound = this.getDiskTags();
            for (n = 0; this.databuf[n] != 0 && n < 64; ++n) {
            }
            this.cmdreg = 0;
            String string = new String(this.databuf, 0, n, StandardCharsets.US_ASCII);
            nBTTagCompound.setString("label", string);
        }
    }

    private void runCmd3() {
        Arrays.fill(this.databuf, (byte)0);
        String s;
        if (this.contents[0].getData() > 0) {
            s = String.format("%016d", this.contents[0].getData());
        }
        else {
            final NBTTagCompound diskTags = this.getDiskTags();
            this.startDisk();
            if (diskTags == null) {
                return;
            }
            s = diskTags.getString("serno");
        }
        final byte[] bytes = s.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(bytes, 0, this.databuf, 0, Math.min(bytes.length, 128));
    }

    private void runCmd4() {
        if (this.sector > 2048) {
            this.cmdreg = -1;
        }
        else {
            final long n = this.sector * 128;
            if (this.contents[0].getData() > 0) {
                InputStream resourceAsStream = null;
                switch (this.contents[0].getData()) {
                    case 1: {
                        resourceAsStream = RedPowerControl.class.getResourceAsStream("/eloraam/control/redforth.img");
                        break;
                    }
                }
                try {
                    if (resourceAsStream.skip(n) == n) {
                        if (resourceAsStream.read(this.databuf) != 128) {
                            this.cmdreg = -1;
                            return;
                        }
                        this.cmdreg = 0;
                    }
                    else {
                        this.cmdreg = -1;
                    }
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                    this.cmdreg = -1;
                }
                finally {
                    try {
                        if (resourceAsStream != null) {
                            resourceAsStream.close();
                        }
                    }
                    catch (IOException ex3) {}
                }
            }
            else {
                final File startDisk = this.startDisk();
                if (startDisk == null) {
                    this.cmdreg = -1;
                }
                else {
                    RandomAccessFile randomAccessFile = null;
                    try {
                        randomAccessFile = new RandomAccessFile(startDisk, "r");
                        randomAccessFile.seek(n);
                        if (randomAccessFile.read(this.databuf) == 128) {
                            this.cmdreg = 0;
                            return;
                        }
                        this.cmdreg = -1;
                    }
                    catch (IOException ex2) {
                        ex2.printStackTrace();
                        this.cmdreg = -1;
                    }
                    finally {
                        try {
                            if (randomAccessFile != null) {
                                randomAccessFile.close();
                            }
                        }
                        catch (IOException ex4) {}
                    }
                }
            }
        }
    }

    private void runCmd5() {
        if (this.contents[0].getData() > 0) {
            this.cmdreg = -1;
        }
        else if (this.sector > 2048) {
            this.cmdreg = -1;
        }
        else {
            final long n = this.sector * 128;
            final File startDisk = this.startDisk();
            if (startDisk == null) {
                this.cmdreg = -1;
            }
            else {
                RandomAccessFile randomAccessFile = null;
                try {
                    randomAccessFile = new RandomAccessFile(startDisk, "rw");
                    randomAccessFile.seek(n);
                    randomAccessFile.write(this.databuf);
                    randomAccessFile.close();
                    randomAccessFile = null;
                    this.cmdreg = 0;
                }
                catch (IOException ex) {
                    this.cmdreg = -1;
                }
                finally {
                    try {
                        if (randomAccessFile != null) {
                            randomAccessFile.close();
                        }
                    }
                    catch (IOException ex2) {}
                }
            }
        }
    }

    private void runDiskCmd() {
        this.dirtyBlock();
        if (this.contents[0] == null) {
            this.cmdreg = -1;
        } else if (!(this.contents[0].getItem() instanceof ItemDisk)) {
            this.cmdreg = -1;
        } else {
            switch (this.cmdreg) {
                case 1: {
                    this.runCmd1();
                    this.cmdreg = 0;
                    break;
                }
                case 2: {
                    this.runCmd2();
                    break;
                }
                case 3: {
                    this.runCmd3();
                    this.cmdreg = 0;
                    break;
                }
                case 4: {
                    this.runCmd4();
                    break;
                }
                case 5: {
                    this.runCmd5();
                    break;
                }
                default: {
                    this.cmdreg = -1;
                }
            }
            this.accessTime = 5;
            if (!this.Active) {
                this.Active = true;
                this.updateBlock();
            }
        }
    }

    private void ejectDisk() {
        if (this.contents[0] != null) {
            MachineLib.ejectItem(this.world, new WorldCoord(this), this.contents[0], CoreLib.rotToSide(this.Rotation) ^ 1);
            this.contents[0] = null;
            this.hasDisk = false;
            this.updateBlock();
        }
    }

    public void update() {
        super.update();
        if (this.contents[0] != null && !(this.contents[0].getItem() instanceof ItemDisk)) {
            this.ejectDisk();
        }
    }

    @Override
    public void q_() {
        if (this.cmdreg != 0 && this.cmdreg != -1) {
            this.runDiskCmd();
        }
        if (this.accessTime > 0 && --this.accessTime == 0) {
            this.Active = false;
            this.updateBlock();
        }
    }

    public int getSize() {
        return 1;
    }

    public ItemStack getItem(int n) {
        return this.contents[n];
    }

    public ItemStack splitStack(int n, int n2) {
        if (this.contents[n] == null) {
            return null;
        }
        if (this.contents[n].count <= n2) {
            ItemStack itemStack = this.contents[n];
            this.contents[n] = null;
            this.update();
            return itemStack;
        }
        ItemStack itemStack = this.contents[n].a(n2);
        if (this.contents[n].count == 0) {
            this.contents[n] = null;
        }
        this.update();
        return itemStack;
    }

    public ItemStack splitWithoutUpdate(int n) {
        if (this.contents[n] == null) {
            return null;
        }
        ItemStack itemStack = this.contents[n];
        this.contents[n] = null;
        return itemStack;
    }

    public void setItem(int n, ItemStack itemStack) {
        this.contents[n] = itemStack;
        if (itemStack != null && itemStack.count > this.getMaxStackSize()) {
            itemStack.count = this.getMaxStackSize();
        }
        this.update();
        this.hasDisk = this.contents[n] != null;
        this.updateBlock();
    }

    public String getName() {
        return "Disk Drive";
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean a(EntityHuman entityHuman) {
        return this.world.getTileEntity(this.x, this.y, this.z) != this ? false : entityHuman.e((double) this.x + 0.5, (double) this.y + 0.5, (double) this.z + 0.5) <= 64.0;
    }

    public void g() {
    }

    public void f() {
    }

    @Override
    public byte[] getFramePacket() {
        Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
        packet211TileDesc.subId = 7;
        this.writeToPacket(packet211TileDesc);
        packet211TileDesc.headout.write(packet211TileDesc.subId);
        return packet211TileDesc.toByteArray();
    }

    @Override
    public void handleFramePacket(byte[] arrby) throws IOException {
        Packet211TileDesc packet211TileDesc = new Packet211TileDesc(arrby);
        packet211TileDesc.subId = packet211TileDesc.getByte();
        this.readFromPacket(packet211TileDesc);
    }

    @Override
    public void onFrameRefresh(IBlockAccess iBlockAccess) {
    }

    @Override
    public void onFramePickup(IBlockAccess iBlockAccess) {
    }

    @Override
    public void onFrameDrop() {
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        this.Rotation = nBTTagCompound.getByte("rot");
        this.accessTime = nBTTagCompound.getByte("actime");
        this.sector = nBTTagCompound.getShort("sect") & 65535;
        this.cmdreg = nBTTagCompound.getByte("cmd") & 255;
        this.rbaddr = nBTTagCompound.getByte("rbaddr") & 255;
        byte by = nBTTagCompound.getByte("fl");
        this.hasDisk = (by & 1) > 0;
        this.Active = (by & 2) > 0;
        this.databuf = nBTTagCompound.getByteArray("dbuf");
        if (this.databuf.length != 128) {
            this.databuf = new byte[128];
        }
        NBTTagList nBTTagList = nBTTagCompound.getList("Items");
        this.contents = new ItemStack[this.getSize()];
        for (int i = 0; i < nBTTagList.size(); ++i) {
            NBTTagCompound nBTTagCompound2 = (NBTTagCompound) nBTTagList.get(i);
            int n = nBTTagCompound2.getByte("Slot") & 255;
            if (n < 0 || n >= this.contents.length)
                continue;
            this.contents[n] = ItemStack.a((NBTTagCompound) nBTTagCompound2);
        }
        this.hasDisk = this.contents[0] != null;
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        nBTTagCompound.setByte("rot", (byte) this.Rotation);
        int n = (this.hasDisk ? 1 : 0) | (this.Active ? 2 : 0);
        nBTTagCompound.setByte("fl", (byte) n);
        nBTTagCompound.setByte("actime", (byte) this.accessTime);
        nBTTagCompound.setByteArray("dbuf", this.databuf);
        nBTTagCompound.setShort("sect", (short) this.sector);
        nBTTagCompound.setByte("cmd", (byte) this.cmdreg);
        nBTTagCompound.setByte("rbaddr", (byte) this.rbaddr);
        NBTTagList nBTTagList = new NBTTagList();
        for (int i = 0; i < this.contents.length; ++i) {
            if (this.contents[i] == null)
                continue;
            NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
            nBTTagCompound2.setByte("Slot", (byte) i);
            this.contents[i].save(nBTTagCompound2);
            nBTTagList.add((NBTBase) nBTTagCompound2);
        }
        nBTTagCompound.set("Items", (NBTBase) nBTTagList);
    }

    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        this.Rotation = packet211TileDesc.getByte();
        int n = packet211TileDesc.getByte();
        this.hasDisk = (n & 1) > 0;
        this.Active = (n & 2) > 0;
    }

    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        packet211TileDesc.addByte(this.Rotation);
        int n = (this.hasDisk ? 1 : 0) | (this.Active ? 2 : 0);
        packet211TileDesc.addByte(n);
    }

    public Packet d() {
        Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
        packet211TileDesc.subId = 7;
        packet211TileDesc.xCoord = this.x;
        packet211TileDesc.yCoord = this.y;
        packet211TileDesc.zCoord = this.z;
        this.writeToPacket(packet211TileDesc);
        packet211TileDesc.encode();
        return packet211TileDesc;
    }

    @Override
    public void handlePacket(Packet211TileDesc packet211TileDesc) {
        try {
            if (packet211TileDesc.subId != 7) {
                return;
            }
            this.readFromPacket(packet211TileDesc);
        } catch (IOException iOException) {
            // empty catch block
        }
        this.world.notify(this.x, this.y, this.z);
    }
}
