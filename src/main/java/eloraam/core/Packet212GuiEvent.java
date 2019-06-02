/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.NetHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Packet212GuiEvent extends PacketVLC {

    public int eventId;
    public int windowId;
    private int cnt1 = 0;
    private int size = 0;

    public void a(DataInputStream dataInputStream) throws IOException {
        this.windowId = dataInputStream.read();
        if (this.windowId == -1) {
            throw new IOException("Not enough data");
        }
        this.eventId = (int) this.readUVLC(dataInputStream);
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
        this.headout.write(this.windowId);
        Packet212GuiEvent.writeUVLC(this.headout, this.eventId);
        Packet212GuiEvent.writeUVLC(this.headout, this.bodyout.size());
        this.size = this.headout.size() + this.bodyout.size();
    }

    public void handle(NetHandler netHandler) {
        CoreProxy.processPacket212(this, netHandler);
    }
}
