/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.NetHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Packet211TileDesc extends PacketVLC {

    public int subId;
    public int xCoord;
    public int yCoord;
    public int zCoord;
    private int cnt1 = 0;
    private int size = 0;

    public Packet211TileDesc() {
        this.lowPriority = true;
    }

    public Packet211TileDesc(byte[] arrby) {
        this.bodyin = new ByteArrayInputStream(arrby);
    }

    public void a(DataInputStream dataInputStream) throws IOException {
        this.subId = dataInputStream.read();
        if (this.subId == -1) {
            throw new IOException("Not enough data");
        }
        this.xCoord = (int) this.readVLC(dataInputStream);
        this.yCoord = (int) this.readVLC(dataInputStream);
        this.zCoord = (int) this.readVLC(dataInputStream);
        int n = (int) this.readUVLC(dataInputStream);
        if (n > 65535) {
            throw new IOException("Packet too big");
        }
        this.size = this.cnt1 + n + 1;
        byte[] arrby = new byte[n];
        int n2 = 0;
        do {
            int n3;
            if ((n3 = dataInputStream.read(arrby, n2, n - n2)) < 1) {
                throw new IOException("Not enough data");
            }
            if (n2 + n3 >= n)
                break;
            n2 += n3;
        } while (true);
        this.bodyin = new ByteArrayInputStream(arrby);
    }

    public void encode() {
        this.headout.write(this.subId);
        Packet211TileDesc.writeVLC(this.headout, this.xCoord);
        Packet211TileDesc.writeVLC(this.headout, this.yCoord);
        Packet211TileDesc.writeVLC(this.headout, this.zCoord);
        Packet211TileDesc.writeUVLC(this.headout, this.bodyout.size());
        this.size = this.headout.size() + this.bodyout.size();
    }

    public void handle(NetHandler netHandler) {
        CoreProxy.processPacket211(this, netHandler);
    }
}
