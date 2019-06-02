/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.Packet;

import java.io.*;

public abstract class PacketVLC extends Packet {

    protected int cnt1 = 0;
    protected int size = 0;
    public ByteArrayOutputStream headout = null;
    public ByteArrayOutputStream bodyout = null;
    public ByteArrayInputStream bodyin = null;

    public PacketVLC() {
        this.headout = new ByteArrayOutputStream();
        this.bodyout = new ByteArrayOutputStream();
    }

    public PacketVLC(byte[] arrby) {
        this.bodyin = new ByteArrayInputStream(arrby);
    }

    public byte[] toByteArray() {
        try {
            this.bodyout.writeTo(this.headout);
        } catch (IOException iOException) {
            // empty catch block
        }
        return this.headout.toByteArray();
    }

    public void a(DataOutputStream dataOutputStream) throws IOException {
        this.headout.writeTo(dataOutputStream);
        this.bodyout.writeTo(dataOutputStream);
    }

    public void addByte(int n) {
        this.bodyout.write(n);
    }

    public void addUVLC(long l) {
        PacketVLC.writeUVLC(this.bodyout, l);
    }

    public void addVLC(long l) {
        PacketVLC.writeVLC(this.bodyout, l);
    }

    public void addByteArray(byte[] arrby) {
        this.addUVLC(arrby.length);
        this.bodyout.write(arrby, 0, arrby.length);
    }

    public int getByte() throws IOException {
        int n = this.bodyin.read();
        if (n < 0) {
            throw new IOException("Not enough data");
        }
        return n;
    }

    public long getUVLC() throws IOException {
        return this.readUVLC(this.bodyin);
    }

    public long getVLC() throws IOException {
        return this.readVLC(this.bodyin);
    }

    public byte[] getByteArray() throws IOException {
        int n = (int) this.getUVLC();
        byte[] arrby = new byte[n];
        this.bodyin.read(arrby, 0, n);
        return arrby;
    }

    protected static void writeVLC(ByteArrayOutputStream byteArrayOutputStream, long l) {
        l = l >= 0 ? (l <<= 1) : -l << 1 | 1;
        PacketVLC.writeUVLC(byteArrayOutputStream, l);
    }

    protected static void writeUVLC(ByteArrayOutputStream byteArrayOutputStream, long l) {
        do {
            int n = (int) (l & 127);
            if ((l >>>= 7) != 0) {
                n |= 128;
            }
            byteArrayOutputStream.write(n);
        } while (l != 0);
    }

    protected long readUVLC(InputStream inputStream) throws IOException {
        long l;
        block2:
        {
            l = 0;
            int n = 0;
            do {
                int n2;
                if ((n2 = inputStream.read()) < 0) {
                    throw new IOException("Not enough data");
                }
                ++this.cnt1;
                l |= (long) ((n2 & 127) << n);
                if ((n2 & 128) == 0)
                    break block2;
            } while ((n += 7) <= 64);
            throw new IOException("Bad VLC");
        }
        return l;
    }

    protected long readVLC(InputStream inputStream) throws IOException {
        long l = this.readUVLC(inputStream);
        l = (l & 1) == 0 ? (l >>>= 1) : -(l >>> 1);
        return l;
    }

    public int a() {
        return this.size;
    }
}
