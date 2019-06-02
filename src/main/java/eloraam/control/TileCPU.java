/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.core.*;
import net.minecraft.server.*;

import java.io.IOException;
import java.io.InputStream;

public class TileCPU extends TileExtended implements IRedbusConnectable, IHandlePackets, IFrameSupport {

    public int Rotation = 0;
    public byte[] memory = new byte[8192];
    int addrPOR;
    int addrBRK;
    int regSP;
    int regPC;
    int regA;
    int regB;
    int regX;
    int regY;
    int regR;
    int regI;
    int regD;
    boolean flagC;
    boolean flagZ;
    boolean flagID;
    boolean flagD;
    boolean flagBRK;
    boolean flagO;
    boolean flagN;
    boolean flagE;
    boolean flagM;
    boolean flagX;
    int mmuRBB = 0;
    int mmuRBA = 0;
    int mmuRBW = 0;
    boolean mmuEnRB = false;
    boolean mmuEnRBW = false;
    private boolean rbTimeout = false;
    private boolean waiTimeout = false;
    public int sliceCycles = -1;
    IRedbusConnectable rbCache = null;
    public int byte0 = 2;
    public int byte1 = 1;
    public int rbaddr = 0;
    TileBackplane[] backplane = new TileBackplane[7];

    public TileCPU() {
        this.coldBootCPU();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void coldBootCPU() {
        this.addrPOR = 8192;
        this.addrBRK = 8192;
        this.regSP = 512;
        this.regPC = 1024;
        this.regR = 768;
        this.regA = 0;
        this.regX = 0;
        this.regY = 0;
        this.regD = 0;
        this.flagC = false;
        this.flagZ = false;
        this.flagID = false;
        this.flagD = false;
        this.flagBRK = false;
        this.flagO = false;
        this.flagN = false;
        this.flagE = true;
        this.flagM = true;
        this.flagX = true;
        this.memory[0] = (byte) this.byte0;
        this.memory[1] = (byte) this.byte1;
        InputStream inputStream = RedPowerControl.class.getResourceAsStream("/eloraam/control/rpcboot.bin");
        try {
            try {
                inputStream.read(this.memory, 1024, 256);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
        this.sliceCycles = -1;
    }

    public void warmBootCPU() {
        if (this.sliceCycles >= 0) {
            this.regSP = 512;
            this.regR = 768;
            this.regPC = this.addrPOR;
        }
        this.sliceCycles = 0;
    }

    public void haltCPU() {
        this.sliceCycles = -1;
    }

    public boolean isRunning() {
        return this.sliceCycles >= 0;
    }

    @Override
    public int rbGetAddr() {
        return this.rbaddr;
    }

    @Override
    public void rbSetAddr(int n) {
    }

    @Override
    public int rbRead(int n) {
        if (!this.mmuEnRBW) {
            return 0;
        }
        return this.readOnlyMem(this.mmuRBW + n);
    }

    @Override
    public void rbWrite(int n, int n2) {
        if (!this.mmuEnRBW) {
            return;
        }
        this.writeOnlyMem(this.mmuRBW + n, n2);
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
            return false;
        }
        if (CoreProxy.isClient(this.world)) {
            return true;
        }
        entityHuman.openGui((BaseMod) mod_RedPowerControl.instance, 3, this.world, this.x, this.y, this.z);
        return true;
    }

    @Override
    public int getBlockID() {
        return RedPowerControl.blockPeripheral.id;
    }

    @Override
    public int getExtendedID() {
        return 1;
    }

    public boolean isUseableByPlayer(EntityHuman entityHuman) {
        if (this.world.getTileEntity(this.x, this.y, this.z) != this) {
            return false;
        }
        return entityHuman.e((double) this.x + 0.5, (double) this.y + 0.5, (double) this.z + 0.5) <= 64.0;
    }

    protected void refreshBackplane() {
        boolean bl = true;
        WorldCoord worldCoord = new WorldCoord(this);
        for (int i = 0; i < 7; ++i) {
            TileBackplane tileBackplane;
            if (!bl) {
                this.backplane[i] = null;
                continue;
            }
            worldCoord.step(CoreLib.rotToSide(this.Rotation));
            this.backplane[i] = tileBackplane = (TileBackplane) CoreLib.getTileEntity((IBlockAccess) this.world, worldCoord, TileBackplane.class);
            if (tileBackplane != null)
                continue;
            bl = false;
        }
    }

    @Override
    public void q_() {
        if (this.sliceCycles < 0) {
            return;
        }
        this.rbTimeout = false;
        this.rbCache = null;
        this.waiTimeout = false;
        this.sliceCycles += 1000;
        if (this.sliceCycles > 100000) {
            this.sliceCycles = 100000;
        }
        this.refreshBackplane();
        while (this.sliceCycles > 0 && !this.waiTimeout && !this.rbTimeout) {
            --this.sliceCycles;
            this.executeInsn();
        }
    }

    protected int readOnlyMem(int n) {
        if ((n &= 65535) < 8192) {
            return this.memory[n] & 255;
        }
        int n2 = (n >> 13) - 1;
        if (this.backplane[n2] == null) {
            return 255;
        }
        return this.backplane[n2].readBackplane(n & 8191);
    }

    public int readMem(int n) {
        if (this.mmuEnRB && n >= this.mmuRBB && n < this.mmuRBB + 256) {
            if (this.rbCache == null) {
                this.rbCache = RedbusLib.getAddr((IBlockAccess) this.world, new WorldCoord(this), this.mmuRBA);
            }
            if (this.rbCache == null) {
                this.rbTimeout = true;
                return 0;
            }
            int n2 = this.rbCache.rbRead(n - this.mmuRBB);
            return n2;
        }
        return this.readOnlyMem(n);
    }

    protected void writeOnlyMem(int n, int n2) {
        if ((n &= 65535) < 8192) {
            this.memory[n] = (byte) n2;
            return;
        }
        int n3 = (n >> 13) - 1;
        if (this.backplane[n3] == null) {
            return;
        }
        this.backplane[n3].writeBackplane(n & 8191, n2);
    }

    public void writeMem(int n, int n2) {
        if (this.mmuEnRB && n >= this.mmuRBB && n < this.mmuRBB + 256) {
            if (this.rbCache == null) {
                this.rbCache = RedbusLib.getAddr((IBlockAccess) this.world, new WorldCoord(this), this.mmuRBA);
            }
            if (this.rbCache == null) {
                this.rbTimeout = true;
                return;
            }
            this.rbCache.rbWrite(n - this.mmuRBB, n2 & 255);
            return;
        }
        this.writeOnlyMem(n, n2);
    }

    private void incPC() {
        this.regPC = this.regPC + 1 & 65535;
    }

    private int maskM() {
        return this.flagM ? 255 : 65535;
    }

    private int maskX() {
        return this.flagX ? 255 : 65535;
    }

    private int negM() {
        return this.flagM ? 128 : 32768;
    }

    private int negX() {
        return this.flagX ? 128 : 32768;
    }

    private int readB() {
        int n = this.readMem(this.regPC);
        this.incPC();
        return n;
    }

    private int readM() {
        int n = this.readMem(this.regPC);
        this.incPC();
        if (!this.flagM) {
            n |= this.readMem(this.regPC) << 8;
            this.incPC();
        }
        return n;
    }

    private int readX() {
        int n = this.readMem(this.regPC);
        this.incPC();
        if (!this.flagX) {
            n |= this.readMem(this.regPC) << 8;
            this.incPC();
        }
        return n;
    }

    private int readM(int n) {
        int n2 = this.readMem(n);
        if (!this.flagM) {
            n2 |= this.readMem(n + 1) << 8;
        }
        return n2;
    }

    private int readX(int n) {
        int n2 = this.readMem(n);
        if (!this.flagX) {
            n2 |= this.readMem(n + 1) << 8;
        }
        return n2;
    }

    private void writeM(int n, int n2) {
        this.writeMem(n, n2);
        if (!this.flagM) {
            this.writeMem(n + 1, n2 >> 8);
        }
    }

    private void writeX(int n, int n2) {
        this.writeMem(n, n2);
        if (!this.flagX) {
            this.writeMem(n + 1, n2 >> 8);
        }
    }

    private int readBX() {
        int n = this.readMem(this.regPC) + this.regX;
        if (this.flagX) {
            n &= 255;
        }
        this.incPC();
        return n;
    }

    private int readBY() {
        int n = this.readMem(this.regPC) + this.regY;
        if (this.flagX) {
            n &= 255;
        }
        this.incPC();
        return n;
    }

    private int readBS() {
        int n = this.readMem(this.regPC) + this.regSP & 65535;
        this.incPC();
        return n;
    }

    private int readBR() {
        int n = this.readMem(this.regPC) + this.regR & 65535;
        this.incPC();
        return n;
    }

    private int readBSWY() {
        int n = this.readMem(this.regPC) + this.regSP & 65535;
        this.incPC();
        return this.readW(n) + this.regY & 65535;
    }

    private int readBRWY() {
        int n = this.readMem(this.regPC) + this.regR & 65535;
        this.incPC();
        return this.readW(n) + this.regY & 65535;
    }

    private int readW() {
        int n = this.readMem(this.regPC);
        this.incPC();
        this.incPC();
        return n |= this.readMem(this.regPC) << 8;
    }

    private int readW(int n) {
        int n2 = this.readMem(n);
        return n2 |= this.readMem(n + 1) << 8;
    }

    private int readWX() {
        int n = this.readMem(this.regPC);
        this.incPC();
        this.incPC();
        return (n |= this.readMem(this.regPC) << 8) + this.regX & 65535;
    }

    private int readWY() {
        int n = this.readMem(this.regPC);
        this.incPC();
        this.incPC();
        return (n |= this.readMem(this.regPC) << 8) + this.regY & 65535;
    }

    private int readWXW() {
        int n = this.readMem(this.regPC);
        this.incPC();
        n |= this.readMem(this.regPC) << 8;
        this.incPC();
        n = n + this.regX & 65535;
        int n2 = this.readMem(n);
        return n2 |= this.readMem(n + 1) << 8;
    }

    private int readBW() {
        int n = this.readMem(this.regPC);
        this.incPC();
        int n2 = this.readMem(n);
        return n2 |= this.readMem(n + 1) << 8;
    }

    private int readWW() {
        int n = this.readMem(this.regPC);
        this.incPC();
        this.incPC();
        int n2 = this.readMem(n |= this.readMem(this.regPC) << 8);
        return n2 |= this.readMem(n + 1) << 8;
    }

    private int readBXW() {
        int n = this.readMem(this.regPC) + this.regX & 255;
        this.incPC();
        int n2 = this.readMem(n);
        return n2 |= this.readMem(n + 1) << 8;
    }

    private int readBWY() {
        int n = this.readMem(this.regPC);
        this.incPC();
        int n2 = this.readMem(n);
        return (n2 |= this.readMem(n + 1) << 8) + this.regY & 65535;
    }

    private void upNZ() {
        this.flagN = (this.regA & this.negM()) > 0;
        this.flagZ = this.regA == 0;
    }

    private void upNZ(int n) {
        this.flagN = (n & this.negM()) > 0;
        this.flagZ = n == 0;
    }

    private void upNZX(int n) {
        this.flagN = (n & this.negX()) > 0;
        this.flagZ = n == 0;
    }

    private void push1(int n) {
        this.regSP = this.flagE ? this.regSP - 1 & 255 | this.regSP & 65280 : this.regSP - 1 & 65535;
        this.writeMem(this.regSP, n);
    }

    private void push1r(int n) {
        this.regR = this.regR - 1 & 65535;
        this.writeMem(this.regR, n);
    }

    private void push2(int n) {
        this.push1(n >> 8);
        this.push1(n & 255);
    }

    private void push2r(int n) {
        this.push1r(n >> 8);
        this.push1r(n & 255);
    }

    private void pushM(int n) {
        if (this.flagM) {
            this.push1(n);
        } else {
            this.push2(n);
        }
    }

    private void pushX(int n) {
        if (this.flagX) {
            this.push1(n);
        } else {
            this.push2(n);
        }
    }

    private void pushMr(int n) {
        if (this.flagM) {
            this.push1r(n);
        } else {
            this.push2r(n);
        }
    }

    private void pushXr(int n) {
        if (this.flagX) {
            this.push1r(n);
        } else {
            this.push2r(n);
        }
    }

    private int pop1() {
        int n = this.readMem(this.regSP);
        this.regSP = this.flagE ? this.regSP + 1 & 255 | this.regSP & 65280 : this.regSP + 1 & 65535;
        return n;
    }

    private int pop1r() {
        int n = this.readMem(this.regR);
        this.regR = this.regR + 1 & 65535;
        return n;
    }

    private int pop2() {
        int n = this.pop1();
        return n |= this.pop1() << 8;
    }

    private int pop2r() {
        int n = this.pop1r();
        return n |= this.pop1r() << 8;
    }

    private int popM() {
        if (this.flagM) {
            return this.pop1();
        }
        return this.pop2();
    }

    private int popMr() {
        if (this.flagM) {
            return this.pop1r();
        }
        return this.pop2r();
    }

    private int popX() {
        if (this.flagX) {
            return this.pop1();
        }
        return this.pop2();
    }

    private int popXr() {
        if (this.flagX) {
            return this.pop1r();
        }
        return this.pop2r();
    }

    private int getFlags() {
        return (this.flagC ? 1 : 0) | (this.flagZ ? 2 : 0) | (this.flagID ? 4 : 0) | (this.flagD ? 8 : 0) | (this.flagX ? 16 : 0) | (this.flagM ? 32 : 0) | (this.flagO ? 64 : 0) | (this.flagN ? 128 : 0);
    }

    private void setFlags(int n) {
        this.flagC = (n & 1) > 0;
        this.flagZ = (n & 2) > 0;
        this.flagID = (n & 4) > 0;
        this.flagD = (n & 8) > 0;
        boolean bl = (n & 32) > 0;
        this.flagO = (n & 64) > 0;
        boolean bl2 = this.flagN = (n & 128) > 0;
        if (this.flagE) {
            this.flagX = false;
            this.flagM = false;
        } else {
            boolean bl3 = this.flagX = (n & 16) > 0;
            if (this.flagX) {
                this.regX &= 255;
                this.regY &= 255;
            }
            if (bl != this.flagM) {
                if (bl) {
                    this.regB = this.regA >> 8;
                    this.regA &= 255;
                } else {
                    this.regA |= this.regB << 8;
                }
                this.flagM = bl;
            }
        }
    }

    private void i_adc(int n) {
        if (this.flagM) {
            if (this.flagD) {
                int n2;
                int n3 = (this.regA & 15) + (n & 15) + (this.flagC ? 1 : 0);
                if (n3 > 9) {
                    n3 = (n3 + 6 & 15) + 16;
                }
                if ((n2 = (this.regA & 240) + (n & 240) + n3) > 160) {
                    n2 += 96;
                }
                this.flagC = n2 > 100;
                this.regA = n2 & 255;
                this.flagO = false;
            } else {
                int n4 = this.regA + n + (this.flagC ? 1 : 0);
                this.flagC = n4 > 255;
                this.flagO = ((n4 ^ this.regA) & (n4 ^ n) & 128) > 0;
                this.regA = n4 & 255;
            }
        } else {
            int n5 = this.regA + n + (this.flagC ? 1 : 0);
            this.flagC = n5 > 65535;
            this.flagO = ((n5 ^ this.regA) & (n5 ^ n) & 32768) > 0;
            this.regA = n5 & 65535;
        }
        this.upNZ();
    }

    private void i_sbc(int n) {
        if (this.flagM) {
            if (this.flagD) {
                int n2;
                int n3 = (this.regA & 15) - (n & 15) + (this.flagC ? 1 : 0) - 1;
                if (n3 < 0) {
                    n3 = (n3 - 6 & 15) - 16;
                }
                if ((n2 = (this.regA & 240) - (n & 240) + n3) < 0) {
                    n2 -= 96;
                }
                this.flagC = n2 < 100;
                this.regA = n2 & 255;
                this.flagO = false;
            } else {
                int n4 = this.regA - n + (this.flagC ? 1 : 0) - 1;
                this.flagC = (n4 & 256) == 0;
                this.flagO = ((n4 ^ this.regA) & (n4 ^ -n) & 128) > 0;
                this.regA = n4 & 255;
            }
        } else {
            int n5 = this.regA - n + (this.flagC ? 1 : 0) - 1;
            this.flagC = (n5 & 65536) == 0;
            this.flagO = ((n5 ^ this.regA) & (n5 ^ -n) & 32768) > 0;
            this.regA = n5 & 65535;
        }
        this.upNZ();
    }

    private void i_mul(int n) {
        if (this.flagM) {
            int n2 = this.flagC ? (byte) n * (byte) this.regA : n * this.regA;
            this.regA = n2 & 255;
            this.regD = n2 >> 8 & 255;
            this.flagN = n2 < 0;
            this.flagZ = n2 == 0;
            this.flagO = this.regD != 0 && this.regD != 255;
        } else {
            long l = this.flagC ? (long) ((short) n * (short) this.regA) : (long) (n * this.regA);
            this.regA = (int) (l & 65535);
            this.regD = (int) (l >> 16 & 65535);
            this.flagN = l < 0;
            this.flagZ = l == 0;
            this.flagO = this.regD != 0 && this.regD != 65535;
        }
    }

    private void i_div(int n) {
        if (n == 0) {
            this.regA = 0;
            this.regD = 0;
            this.flagO = true;
            this.flagZ = false;
            this.flagN = false;
            return;
        }
        if (this.flagM) {
            int n2;
            if (this.flagC) {
                n2 = (byte) this.regD << 8 | this.regA;
                n = (byte) n;
            } else {
                n2 = this.regD << 8 | this.regA;
            }
            this.regD = n2 % n & 255;
            this.regA = (n2 /= n) & 255;
            this.flagO = this.flagC ? n2 > 127 || n2 < -128 : n2 > 255;
            this.flagZ = this.regA == 0;
            this.flagN = n2 < 0;
        } else if (this.flagC) {
            int n3 = (short) this.regD << 16 | this.regA;
            n = (short) n;
            this.regD = n3 % n & 65535;
            this.regA = (n3 /= n) & 65535;
            this.flagO = n3 > 32767 || n3 < -32768;
            this.flagZ = this.regA == 0;
            this.flagN = n3 < 0;
        } else {
            long l = this.regD << 16 | this.regA;
            this.regD = (int) (l % (long) n & 65535);
            this.regA = (int) ((l /= (long) n) & 65535);
            this.flagO = l > 65535;
            this.flagZ = this.regA == 0;
            this.flagN = l < 0;
        }
    }

    private void i_and(int n) {
        this.regA &= n;
        this.upNZ();
    }

    private void i_asl(int n) {
        int n2 = this.readM(n);
        this.flagC = (n2 & this.negM()) > 0;
        n2 = n2 << 1 & this.maskM();
        this.upNZ(n2);
        this.writeM(n, n2);
    }

    private void i_lsr(int n) {
        int n2 = this.readM(n);
        this.flagC = (n2 & 1) > 0;
        this.upNZ(n2 >>>= 1);
        this.writeM(n, n2);
    }

    private void i_rol(int n) {
        int n2 = this.readM(n);
        int n3 = (n2 << 1 | (this.flagC ? 1 : 0)) & this.maskM();
        this.flagC = (n2 & this.negM()) > 0;
        this.upNZ(n3);
        this.writeM(n, n3);
    }

    private void i_ror(int n) {
        int n2 = this.readM(n);
        int n3 = n2 >>> 1 | (this.flagC ? this.negM() : 0);
        this.flagC = (n2 & 1) > 0;
        this.upNZ(n3);
        this.writeM(n, n3);
    }

    private void i_brc(boolean bl) {
        int n = this.readB();
        if (bl) {
            this.regPC = this.regPC + (byte) n & 65535;
        }
    }

    private void i_bit(int n) {
        if (this.flagM) {
            this.flagO = (n & 64) > 0;
            this.flagN = (n & 128) > 0;
        } else {
            this.flagO = (n & 16384) > 0;
            this.flagN = (n & 32768) > 0;
        }
        this.flagZ = (n & this.regA) > 0;
    }

    private void i_trb(int n) {
        this.flagZ = (n & this.regA) > 0;
        this.regA &= ~n;
    }

    private void i_tsb(int n) {
        this.flagZ = (n & this.regA) > 0;
        this.regA |= n;
    }

    private void i_cmp(int n, int n2) {
        this.flagC = (n -= n2) >= 0;
        this.flagZ = n == 0;
        this.flagN = (n & this.negM()) > 0;
    }

    private void i_cmpx(int n, int n2) {
        this.flagC = (n -= n2) >= 0;
        this.flagZ = n == 0;
        this.flagN = (n & this.negX()) > 0;
    }

    private void i_dec(int n) {
        int n2 = this.readM(n);
        n2 = n2 - 1 & this.maskM();
        this.writeM(n, n2);
        this.upNZ(n2);
    }

    private void i_inc(int n) {
        int n2 = this.readM(n);
        n2 = n2 + 1 & this.maskM();
        this.writeM(n, n2);
        this.upNZ(n2);
    }

    private void i_eor(int n) {
        this.regA ^= n;
        this.upNZ();
    }

    private void i_or(int n) {
        this.regA |= n;
        this.upNZ();
    }

    private void i_mmu(int n) {
        switch (n) {
            default: {
                break;
            }
            case 0: {
                int n2 = this.regA & 255;
                if (this.mmuRBA == n2)
                    break;
                if (this.rbCache != null) {
                    this.rbTimeout = true;
                }
                this.mmuRBA = n2;
                break;
            }
            case 128: {
                this.regA = this.mmuRBA;
                break;
            }
            case 1: {
                this.mmuRBB = this.regA;
                break;
            }
            case 129: {
                this.regA = this.mmuRBB;
                if (!this.flagM)
                    break;
                this.regB = this.regA >> 8;
                this.regA &= 255;
                break;
            }
            case 2: {
                this.mmuEnRB = true;
                break;
            }
            case 130: {
                this.mmuEnRB = false;
                break;
            }
            case 3: {
                this.mmuRBW = this.regA;
                break;
            }
            case 131: {
                this.regA = this.mmuRBW;
                if (!this.flagM)
                    break;
                this.regB = this.regA >> 8;
                this.regA &= 255;
                break;
            }
            case 4: {
                this.mmuEnRBW = true;
                break;
            }
            case 132: {
                this.mmuEnRBW = false;
                break;
            }
            case 5: {
                this.addrBRK = this.regA;
                break;
            }
            case 133: {
                this.regA = this.addrBRK;
                if (!this.flagM)
                    break;
                this.regB = this.regA >> 8;
                this.regA &= 255;
                break;
            }
            case 6: {
                this.addrPOR = this.regA;
                break;
            }
            case 134: {
                this.regA = this.addrPOR;
                if (!this.flagM)
                    break;
                this.regB = this.regA >> 8;
                this.regA &= 255;
            }
        }
    }

    public void executeInsn() {
        int n = this.readMem(this.regPC);
        this.incPC();
        switch (n) {
            default: {
                break;
            }
            case 105: {
                this.i_adc(this.readM());
                break;
            }
            case 101: {
                this.i_adc(this.readM(this.readB()));
                break;
            }
            case 117: {
                this.i_adc(this.readM(this.readBX()));
                break;
            }
            case 109: {
                this.i_adc(this.readM(this.readW()));
                break;
            }
            case 125: {
                this.i_adc(this.readM(this.readWX()));
                break;
            }
            case 121: {
                this.i_adc(this.readM(this.readWY()));
                break;
            }
            case 97: {
                this.i_adc(this.readM(this.readBXW()));
                break;
            }
            case 113: {
                this.i_adc(this.readM(this.readBWY()));
                break;
            }
            case 114: {
                this.i_adc(this.readM(this.readBW()));
                break;
            }
            case 99: {
                this.i_adc(this.readM(this.readBS()));
                break;
            }
            case 115: {
                this.i_adc(this.readM(this.readBSWY()));
                break;
            }
            case 103: {
                this.i_adc(this.readM(this.readBR()));
                break;
            }
            case 119: {
                this.i_adc(this.readM(this.readBRWY()));
                break;
            }
            case 41: {
                this.i_and(this.readM());
                break;
            }
            case 37: {
                this.i_and(this.readM(this.readB()));
                break;
            }
            case 53: {
                this.i_and(this.readM(this.readBX()));
                break;
            }
            case 45: {
                this.i_and(this.readM(this.readW()));
                break;
            }
            case 61: {
                this.i_and(this.readM(this.readWX()));
                break;
            }
            case 57: {
                this.i_and(this.readM(this.readWY()));
                break;
            }
            case 33: {
                this.i_and(this.readM(this.readBXW()));
                break;
            }
            case 49: {
                this.i_and(this.readM(this.readBWY()));
                break;
            }
            case 50: {
                this.i_and(this.readM(this.readBW()));
                break;
            }
            case 35: {
                this.i_and(this.readM(this.readBS()));
                break;
            }
            case 51: {
                this.i_and(this.readM(this.readBSWY()));
                break;
            }
            case 39: {
                this.i_and(this.readM(this.readBR()));
                break;
            }
            case 55: {
                this.i_and(this.readM(this.readBRWY()));
                break;
            }
            case 10: {
                this.flagC = (this.regA & this.negM()) > 0;
                this.regA = this.regA << 1 & this.maskM();
                this.upNZ();
                break;
            }
            case 6: {
                this.i_asl(this.readB());
                break;
            }
            case 22: {
                this.i_asl(this.readBX());
                break;
            }
            case 14: {
                this.i_asl(this.readW());
                break;
            }
            case 30: {
                this.i_asl(this.readWX());
                break;
            }
            case 144: {
                this.i_brc(!this.flagC);
                break;
            }
            case 176: {
                this.i_brc(this.flagC);
                break;
            }
            case 240: {
                this.i_brc(this.flagZ);
                break;
            }
            case 48: {
                this.i_brc(this.flagN);
                break;
            }
            case 208: {
                this.i_brc(!this.flagZ);
                break;
            }
            case 16: {
                this.i_brc(!this.flagN);
                break;
            }
            case 80: {
                this.i_brc(!this.flagO);
                break;
            }
            case 112: {
                this.i_brc(this.flagO);
                break;
            }
            case 128: {
                this.i_brc(true);
                break;
            }
            case 137: {
                this.flagZ = (this.readM() & this.regA) == 0;
                break;
            }
            case 36: {
                this.i_bit(this.readM(this.readB()));
                break;
            }
            case 52: {
                this.i_bit(this.readM(this.readBX()));
                break;
            }
            case 44: {
                this.i_bit(this.readM(this.readW()));
                break;
            }
            case 60: {
                this.i_bit(this.readM(this.readWX()));
                break;
            }
            case 0: {
                this.push2(this.regPC);
                this.push1(this.getFlags());
                this.flagBRK = true;
                this.regPC = this.addrBRK;
                break;
            }
            case 24: {
                this.flagC = false;
                break;
            }
            case 216: {
                this.flagD = false;
                break;
            }
            case 88: {
                this.flagID = false;
                break;
            }
            case 184: {
                this.flagO = false;
                break;
            }
            case 201: {
                this.i_cmp(this.regA, this.readM());
                break;
            }
            case 197: {
                this.i_cmp(this.regA, this.readM(this.readB()));
                break;
            }
            case 213: {
                this.i_cmp(this.regA, this.readM(this.readBX()));
                break;
            }
            case 205: {
                this.i_cmp(this.regA, this.readM(this.readW()));
                break;
            }
            case 221: {
                this.i_cmp(this.regA, this.readM(this.readWX()));
                break;
            }
            case 217: {
                this.i_cmp(this.regA, this.readM(this.readWY()));
                break;
            }
            case 193: {
                this.i_cmp(this.regA, this.readM(this.readBXW()));
                break;
            }
            case 209: {
                this.i_cmp(this.regA, this.readM(this.readBWY()));
                break;
            }
            case 210: {
                this.i_cmp(this.regA, this.readM(this.readBW()));
                break;
            }
            case 195: {
                this.i_cmp(this.regA, this.readM(this.readBS()));
                break;
            }
            case 211: {
                this.i_cmp(this.regA, this.readM(this.readBSWY()));
                break;
            }
            case 199: {
                this.i_cmp(this.regA, this.readM(this.readBR()));
                break;
            }
            case 215: {
                this.i_cmp(this.regA, this.readM(this.readBRWY()));
                break;
            }
            case 224: {
                this.i_cmpx(this.regX, this.readX());
                break;
            }
            case 228: {
                this.i_cmpx(this.regX, this.readX(this.readB()));
                break;
            }
            case 236: {
                this.i_cmpx(this.regX, this.readX(this.readW()));
                break;
            }
            case 192: {
                this.i_cmpx(this.regY, this.readX());
                break;
            }
            case 196: {
                this.i_cmpx(this.regY, this.readX(this.readB()));
                break;
            }
            case 204: {
                this.i_cmpx(this.regY, this.readX(this.readW()));
                break;
            }
            case 58: {
                this.regA = this.regA - 1 & this.maskM();
                this.upNZ(this.regA);
                break;
            }
            case 198: {
                this.i_dec(this.readB());
                break;
            }
            case 214: {
                this.i_dec(this.readBX());
                break;
            }
            case 206: {
                this.i_dec(this.readW());
                break;
            }
            case 222: {
                this.i_dec(this.readWX());
                break;
            }
            case 202: {
                this.regX = this.regX - 1 & this.maskX();
                this.upNZ(this.regX);
                break;
            }
            case 136: {
                this.regY = this.regY - 1 & this.maskX();
                this.upNZ(this.regY);
                break;
            }
            case 73: {
                this.i_eor(this.readM());
                break;
            }
            case 69: {
                this.i_eor(this.readM(this.readB()));
                break;
            }
            case 85: {
                this.i_eor(this.readM(this.readBX()));
                break;
            }
            case 77: {
                this.i_eor(this.readM(this.readW()));
                break;
            }
            case 93: {
                this.i_eor(this.readM(this.readWX()));
                break;
            }
            case 89: {
                this.i_eor(this.readM(this.readWY()));
                break;
            }
            case 65: {
                this.i_eor(this.readM(this.readBXW()));
                break;
            }
            case 81: {
                this.i_eor(this.readM(this.readBWY()));
                break;
            }
            case 82: {
                this.i_eor(this.readM(this.readBW()));
                break;
            }
            case 67: {
                this.i_eor(this.readM(this.readBS()));
                break;
            }
            case 83: {
                this.i_eor(this.readM(this.readBSWY()));
                break;
            }
            case 71: {
                this.i_eor(this.readM(this.readBR()));
                break;
            }
            case 87: {
                this.i_eor(this.readM(this.readBRWY()));
                break;
            }
            case 26: {
                this.regA = this.regA + 1 & this.maskM();
                this.upNZ(this.regA);
                break;
            }
            case 230: {
                this.i_inc(this.readB());
                break;
            }
            case 246: {
                this.i_inc(this.readBX());
                break;
            }
            case 238: {
                this.i_inc(this.readW());
                break;
            }
            case 254: {
                this.i_inc(this.readWX());
                break;
            }
            case 232: {
                this.regX = this.regX + 1 & this.maskX();
                this.upNZ(this.regX);
                break;
            }
            case 200: {
                this.regY = this.regY + 1 & this.maskX();
                this.upNZ(this.regY);
                break;
            }
            case 76: {
                this.regPC = this.readW();
                break;
            }
            case 108: {
                this.regPC = this.readWW();
                break;
            }
            case 124: {
                this.regPC = this.readWXW();
                break;
            }
            case 32: {
                this.push2(this.regPC + 1);
                this.regPC = this.readW();
                break;
            }
            case 252: {
                this.push2(this.regPC + 1);
                this.regPC = this.readWXW();
                break;
            }
            case 169: {
                this.regA = this.readM();
                this.upNZ();
                break;
            }
            case 165: {
                this.regA = this.readM(this.readB());
                this.upNZ();
                break;
            }
            case 181: {
                this.regA = this.readM(this.readBX());
                this.upNZ();
                break;
            }
            case 173: {
                this.regA = this.readM(this.readW());
                this.upNZ();
                break;
            }
            case 189: {
                this.regA = this.readM(this.readWX());
                this.upNZ();
                break;
            }
            case 185: {
                this.regA = this.readM(this.readWY());
                this.upNZ();
                break;
            }
            case 161: {
                this.regA = this.readM(this.readBXW());
                this.upNZ();
                break;
            }
            case 177: {
                this.regA = this.readM(this.readBWY());
                this.upNZ();
                break;
            }
            case 178: {
                this.regA = this.readM(this.readBW());
                this.upNZ();
                break;
            }
            case 163: {
                this.regA = this.readM(this.readBS());
                this.upNZ();
                break;
            }
            case 179: {
                this.regA = this.readM(this.readBSWY());
                this.upNZ();
                break;
            }
            case 167: {
                this.regA = this.readM(this.readBR());
                this.upNZ();
                break;
            }
            case 183: {
                this.regA = this.readM(this.readBRWY());
                this.upNZ();
                break;
            }
            case 162: {
                this.regX = this.readX();
                this.upNZ(this.regX);
                break;
            }
            case 166: {
                this.regX = this.readX(this.readB());
                this.upNZ(this.regX);
                break;
            }
            case 182: {
                this.regX = this.readX(this.readBY());
                this.upNZ(this.regX);
                break;
            }
            case 174: {
                this.regX = this.readX(this.readW());
                this.upNZ(this.regX);
                break;
            }
            case 190: {
                this.regX = this.readX(this.readWY());
                this.upNZ(this.regX);
                break;
            }
            case 160: {
                this.regY = this.readX();
                this.upNZ(this.regY);
                break;
            }
            case 164: {
                this.regY = this.readX(this.readB());
                this.upNZ(this.regY);
                break;
            }
            case 180: {
                this.regY = this.readX(this.readBX());
                this.upNZ(this.regY);
                break;
            }
            case 172: {
                this.regY = this.readX(this.readW());
                this.upNZ(this.regY);
                break;
            }
            case 188: {
                this.regY = this.readX(this.readWX());
                this.upNZ(this.regY);
                break;
            }
            case 74: {
                this.flagC = (this.regA & 1) > 0;
                this.regA >>>= 1;
                this.upNZ();
                break;
            }
            case 70: {
                this.i_lsr(this.readB());
                break;
            }
            case 86: {
                this.i_lsr(this.readBX());
                break;
            }
            case 78: {
                this.i_lsr(this.readW());
                break;
            }
            case 94: {
                this.i_lsr(this.readWX());
                break;
            }
            case 9: {
                this.i_or(this.readM());
                break;
            }
            case 5: {
                this.i_or(this.readM(this.readB()));
                break;
            }
            case 21: {
                this.i_or(this.readM(this.readBX()));
                break;
            }
            case 13: {
                this.i_or(this.readM(this.readW()));
                break;
            }
            case 29: {
                this.i_or(this.readM(this.readWX()));
                break;
            }
            case 25: {
                this.i_or(this.readM(this.readWY()));
                break;
            }
            case 1: {
                this.i_or(this.readM(this.readBXW()));
                break;
            }
            case 17: {
                this.i_or(this.readM(this.readBWY()));
                break;
            }
            case 18: {
                this.i_or(this.readM(this.readBW()));
                break;
            }
            case 3: {
                this.i_or(this.readM(this.readBS()));
                break;
            }
            case 19: {
                this.i_or(this.readM(this.readBSWY()));
                break;
            }
            case 7: {
                this.i_or(this.readM(this.readBR()));
                break;
            }
            case 23: {
                this.i_or(this.readM(this.readBRWY()));
                break;
            }
            case 72: {
                this.pushM(this.regA);
                break;
            }
            case 8: {
                this.push1(this.getFlags());
                break;
            }
            case 218: {
                this.pushX(this.regX);
                break;
            }
            case 90: {
                this.pushX(this.regY);
                break;
            }
            case 104: {
                this.regA = this.popM();
                this.upNZ();
                break;
            }
            case 40: {
                this.setFlags(this.pop1());
                break;
            }
            case 250: {
                this.regX = this.popX();
                this.upNZX(this.regX);
                break;
            }
            case 122: {
                this.regY = this.popX();
                this.upNZX(this.regY);
                break;
            }
            case 42: {
                int n2 = (this.regA << 1 | (this.flagC ? 1 : 0)) & this.maskM();
                this.flagC = (this.regA & this.negM()) > 0;
                this.regA = n2;
                this.upNZ();
                break;
            }
            case 38: {
                this.i_rol(this.readB());
                break;
            }
            case 54: {
                this.i_rol(this.readBX());
                break;
            }
            case 46: {
                this.i_rol(this.readW());
                break;
            }
            case 62: {
                this.i_rol(this.readWX());
                break;
            }
            case 106: {
                int n3 = this.regA >>> 1 | (this.flagC ? this.negM() : 0);
                this.flagC = (this.regA & 1) > 0;
                this.regA = n3;
                this.upNZ();
                break;
            }
            case 102: {
                this.i_ror(this.readB());
                break;
            }
            case 118: {
                this.i_ror(this.readBX());
                break;
            }
            case 110: {
                this.i_ror(this.readW());
                break;
            }
            case 126: {
                this.i_ror(this.readWX());
                break;
            }
            case 64: {
                this.setFlags(this.pop1());
                this.regPC = this.pop2();
                break;
            }
            case 96: {
                this.regPC = this.pop2() + 1;
                break;
            }
            case 233: {
                this.i_sbc(this.readM());
                break;
            }
            case 229: {
                this.i_sbc(this.readM(this.readB()));
                break;
            }
            case 245: {
                this.i_sbc(this.readM(this.readBX()));
                break;
            }
            case 237: {
                this.i_sbc(this.readM(this.readW()));
                break;
            }
            case 253: {
                this.i_sbc(this.readM(this.readWX()));
                break;
            }
            case 249: {
                this.i_sbc(this.readM(this.readWY()));
                break;
            }
            case 225: {
                this.i_sbc(this.readM(this.readBXW()));
                break;
            }
            case 241: {
                this.i_sbc(this.readM(this.readBWY()));
                break;
            }
            case 242: {
                this.i_sbc(this.readM(this.readBW()));
                break;
            }
            case 227: {
                this.i_sbc(this.readM(this.readBS()));
                break;
            }
            case 243: {
                this.i_sbc(this.readM(this.readBSWY()));
                break;
            }
            case 231: {
                this.i_sbc(this.readM(this.readBR()));
                break;
            }
            case 247: {
                this.i_sbc(this.readM(this.readBRWY()));
                break;
            }
            case 56: {
                this.flagC = true;
                break;
            }
            case 248: {
                this.flagD = true;
                break;
            }
            case 120: {
                this.flagID = true;
                break;
            }
            case 133: {
                this.writeM(this.readB(), this.regA);
                break;
            }
            case 149: {
                this.writeM(this.readBX(), this.regA);
                break;
            }
            case 141: {
                this.writeM(this.readW(), this.regA);
                break;
            }
            case 157: {
                this.writeM(this.readWX(), this.regA);
                break;
            }
            case 153: {
                this.writeM(this.readWY(), this.regA);
                break;
            }
            case 129: {
                this.writeM(this.readBXW(), this.regA);
                break;
            }
            case 145: {
                this.writeM(this.readBWY(), this.regA);
                break;
            }
            case 146: {
                this.writeM(this.readBW(), this.regA);
                break;
            }
            case 131: {
                this.writeM(this.readBS(), this.regA);
                break;
            }
            case 147: {
                this.writeM(this.readBSWY(), this.regA);
                break;
            }
            case 135: {
                this.writeM(this.readBR(), this.regA);
                break;
            }
            case 151: {
                this.writeM(this.readBRWY(), this.regA);
                break;
            }
            case 134: {
                this.writeX(this.readB(), this.regX);
                break;
            }
            case 150: {
                this.writeX(this.readBY(), this.regX);
                break;
            }
            case 142: {
                this.writeX(this.readW(), this.regX);
                break;
            }
            case 132: {
                this.writeX(this.readB(), this.regY);
                break;
            }
            case 148: {
                this.writeX(this.readBX(), this.regY);
                break;
            }
            case 140: {
                this.writeX(this.readW(), this.regY);
                break;
            }
            case 170: {
                this.regX = this.regA;
                if (this.flagX) {
                    this.regX &= 255;
                }
                this.upNZX(this.regX);
                break;
            }
            case 168: {
                this.regY = this.regA;
                if (this.flagX) {
                    this.regY &= 255;
                }
                this.upNZX(this.regY);
                break;
            }
            case 186: {
                this.regX = this.regSP;
                if (this.flagX) {
                    this.regX &= 255;
                }
                this.upNZX(this.regX);
                break;
            }
            case 138: {
                this.regA = this.regX;
                if (this.flagM) {
                    this.regA &= 255;
                }
                this.upNZ();
                break;
            }
            case 154: {
                this.regSP = this.flagX ? this.regSP & 65280 | this.regX & 255 : this.regX;
                this.upNZX(this.regX);
                break;
            }
            case 152: {
                this.regA = this.regY;
                if (this.flagM) {
                    this.regA &= 255;
                }
                this.upNZX(this.regY);
                break;
            }
            case 100: {
                this.writeM(this.readB(), 0);
                break;
            }
            case 116: {
                this.writeM(this.readBX(), 0);
                break;
            }
            case 156: {
                this.writeM(this.readW(), 0);
                break;
            }
            case 158: {
                this.writeM(this.readWX(), 0);
                break;
            }
            case 20: {
                this.i_trb(this.readM(this.readB()));
                break;
            }
            case 28: {
                this.i_trb(this.readM(this.readW()));
                break;
            }
            case 4: {
                this.i_tsb(this.readM(this.readB()));
                break;
            }
            case 12: {
                this.i_tsb(this.readM(this.readW()));
                break;
            }
            case 219: {
                this.sliceCycles = -1;
                if (!this.world.isEmpty(this.x, this.y + 1, this.z))
                    break;
                this.world.makeSound((double) this.x + 0.5, (double) this.y + 0.5, (double) this.z + 0.5, "fire.ignite", 1.0f, this.world.random.nextFloat() * 0.4f + 0.8f);
                this.world.setTypeId(this.x, this.y + 1, this.z, Block.FIRE.id);
                break;
            }
            case 203: {
                this.waiTimeout = true;
                break;
            }
            case 155: {
                this.regY = this.regX;
                this.upNZX(this.regY);
                break;
            }
            case 187: {
                this.regX = this.regY;
                this.upNZX(this.regX);
                break;
            }
            case 244: {
                this.push2(this.readW());
                break;
            }
            case 212: {
                this.push2(this.readBW());
                break;
            }
            case 98: {
                int n4 = this.readB();
                this.push2(this.regPC + n4);
                break;
            }
            case 235: {
                if (this.flagM) {
                    int n5 = this.regA;
                    this.regA = this.regB;
                    this.regB = n5;
                    break;
                }
                this.regA = this.regA >> 8 & 255 | this.regA << 8 & 65280;
                break;
            }
            case 251: {
                if (this.flagE == this.flagC)
                    break;
                if (this.flagE) {
                    this.flagE = false;
                    this.flagC = true;
                    break;
                }
                this.flagE = true;
                this.flagC = false;
                if (!this.flagM) {
                    this.regB = this.regA >> 8;
                }
                this.flagM = true;
                this.flagX = true;
                this.regA &= 255;
                this.regX &= 255;
                this.regY &= 255;
                break;
            }
            case 194: {
                this.setFlags(this.getFlags() & ~this.readB());
                break;
            }
            case 226: {
                this.setFlags(this.getFlags() | this.readB());
                break;
            }
            case 139: {
                if (this.flagX) {
                    this.regSP = this.regR & 65280 | this.regX & 255;
                } else {
                    this.regR = this.regX;
                }
                this.upNZX(this.regR);
                break;
            }
            case 171: {
                this.regX = this.regR;
                if (this.flagX) {
                    this.regX &= 255;
                }
                this.upNZX(this.regX);
                break;
            }
            case 68: {
                this.push2r(this.readW());
                break;
            }
            case 84: {
                this.push2r(this.readBW());
                break;
            }
            case 130: {
                int n6 = this.readB();
                this.push2r(this.regPC + n6);
                break;
            }
            case 59: {
                this.regX = this.popXr();
                this.upNZX(this.regX);
                break;
            }
            case 107: {
                this.regA = this.popMr();
                this.upNZ(this.regA);
                break;
            }
            case 123: {
                this.regY = this.popXr();
                this.upNZX(this.regY);
                break;
            }
            case 27: {
                this.pushXr(this.regX);
                break;
            }
            case 75: {
                this.pushMr(this.regA);
                break;
            }
            case 91: {
                this.pushXr(this.regY);
                break;
            }
            case 11: {
                this.push2r(this.regI);
                break;
            }
            case 43: {
                this.regI = this.pop2r();
                this.upNZX(this.regI);
                break;
            }
            case 92: {
                this.regI = this.regX;
                this.upNZX(this.regX);
                break;
            }
            case 220: {
                this.regX = this.regI;
                if (this.flagX) {
                    this.regX &= 255;
                }
                this.upNZX(this.regX);
                break;
            }
            case 2: {
                this.regPC = this.readW(this.regI);
                this.regI += 2;
                break;
            }
            case 66: {
                if (this.flagM) {
                    this.regA = this.readMem(this.regI);
                    ++this.regI;
                    break;
                }
                this.regA = this.readW(this.regI);
                this.regI += 2;
                break;
            }
            case 34: {
                this.push2r(this.regI);
                this.regI = this.regPC + 2;
                this.regPC = this.readW(this.regPC);
                break;
            }
            case 15: {
                this.i_mul(this.readM(this.readB()));
                break;
            }
            case 31: {
                this.i_mul(this.readM(this.readBX()));
                break;
            }
            case 47: {
                this.i_mul(this.readM(this.readW()));
                break;
            }
            case 63: {
                this.i_mul(this.readM(this.readWX()));
                break;
            }
            case 79: {
                this.i_div(this.readM(this.readB()));
                break;
            }
            case 95: {
                this.i_div(this.readM(this.readBX()));
                break;
            }
            case 111: {
                this.i_div(this.readM(this.readW()));
                break;
            }
            case 127: {
                this.i_div(this.readM(this.readWX()));
                break;
            }
            case 143: {
                this.regD = 0;
                this.regB = 0;
                break;
            }
            case 159: {
                this.regD = (this.regA & this.negM()) <= 0 ? 0 : 65535;
                this.regB = this.regD & 255;
                break;
            }
            case 175: {
                this.regA = this.regD;
                if (this.flagM) {
                    this.regA &= 255;
                }
                this.upNZ(this.regA);
                break;
            }
            case 191: {
                this.regD = this.flagM ? this.regA | this.regB << 8 : this.regA;
                this.upNZ(this.regA);
                break;
            }
            case 207: {
                this.regD = this.popM();
                break;
            }
            case 223: {
                this.pushM(this.regD);
                break;
            }
            case 239: {
                this.i_mmu(this.readB());
            }
        }
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
        this.memory = nBTTagCompound.getByteArray("ram");
        if (this.memory.length != 8192) {
            this.memory = new byte[8192];
        }
        this.Rotation = nBTTagCompound.getByte("rot");
        this.addrPOR = nBTTagCompound.getShort("por") & 65535;
        this.addrBRK = nBTTagCompound.getShort("brk") & 65535;
        byte by = nBTTagCompound.getByte("efl");
        this.flagE = (by & 1) > 0;
        this.mmuEnRB = (by & 2) > 0;
        this.mmuEnRBW = (by & 4) > 0;
        this.setFlags(nBTTagCompound.getByte("fl"));
        this.regSP = nBTTagCompound.getShort("rsp") & 65535;
        this.regPC = nBTTagCompound.getShort("rpc") & 65535;
        this.regA = nBTTagCompound.getShort("ra") & 65535;
        if (this.flagM) {
            this.regB = this.regA >> 8;
            this.regA &= 255;
        }
        this.regX = nBTTagCompound.getShort("rx") & 65535;
        this.regY = nBTTagCompound.getShort("ry") & 65535;
        this.regD = nBTTagCompound.getShort("rd") & 65535;
        this.regR = nBTTagCompound.getShort("rr") & 65535;
        this.regI = nBTTagCompound.getShort("ri") & 65535;
        this.mmuRBB = nBTTagCompound.getShort("mmrb") & 65535;
        this.mmuRBW = nBTTagCompound.getShort("mmrbw") & 65535;
        this.mmuRBA = nBTTagCompound.getByte("mmra") & 255;
        this.sliceCycles = nBTTagCompound.getInt("cyc");
        this.byte0 = nBTTagCompound.getByte("b0") & 255;
        this.byte1 = nBTTagCompound.getByte("b1") & 255;
        this.rbaddr = nBTTagCompound.getByte("rbaddr") & 255;
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        nBTTagCompound.setByte("rot", (byte) this.Rotation);
        nBTTagCompound.setByteArray("ram", this.memory);
        nBTTagCompound.setShort("por", (short) this.addrPOR);
        nBTTagCompound.setShort("brk", (short) this.addrBRK);
        int n = (this.flagE ? 1 : 0) | (this.mmuEnRB ? 2 : 0) | (this.mmuEnRBW ? 4 : 0);
        nBTTagCompound.setByte("efl", (byte) n);
        nBTTagCompound.setByte("fl", (byte) this.getFlags());
        nBTTagCompound.setShort("rsp", (short) this.regSP);
        nBTTagCompound.setShort("rpc", (short) this.regPC);
        if (this.flagM) {
            this.regA = this.regA & 255 | this.regB << 8;
        }
        nBTTagCompound.setShort("ra", (short) this.regA);
        if (this.flagM) {
            this.regA &= 255;
        }
        nBTTagCompound.setShort("rx", (short) this.regX);
        nBTTagCompound.setShort("ry", (short) this.regY);
        nBTTagCompound.setShort("rd", (short) this.regD);
        nBTTagCompound.setShort("rr", (short) this.regR);
        nBTTagCompound.setShort("ri", (short) this.regI);
        nBTTagCompound.setShort("mmrb", (short) this.mmuRBB);
        nBTTagCompound.setShort("mmrbw", (short) this.mmuRBW);
        nBTTagCompound.setByte("mmra", (byte) this.mmuRBA);
        nBTTagCompound.setInt("cyc", this.sliceCycles);
        nBTTagCompound.setByte("b0", (byte) this.byte0);
        nBTTagCompound.setByte("b1", (byte) this.byte1);
        nBTTagCompound.setByte("rbaddr", (byte) this.rbaddr);
    }

    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        this.Rotation = packet211TileDesc.getByte();
    }

    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        packet211TileDesc.addByte(this.Rotation);
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
