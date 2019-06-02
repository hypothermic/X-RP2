/* X-RP - decompiled with CFR */
package dreadend.RP2.mutithread;

import java.util.concurrent.ConcurrentHashMap;

public class PacketRestrictor {

    private static ConcurrentHashMap<String, Long> map = new ConcurrentHashMap();
    private static Thread cleaner = new Thread(new Runnable() {

        @Override
        public void run() {
            Thread.currentThread().setName("RP2-PacketRestrictor-Cleaner");
            do {
                map.clear();
                try {
                    Thread.sleep(50000);
                } catch (InterruptedException interruptedException) {
                }
            } while (true);
        }
    });

    static {
        cleaner.start();
    }

    public static boolean checkTileEntityPacket(String string) {
        boolean sendPacket = false;
        if (map.containsKey(string)) {
            Long time = map.get(string) + 10 - System.currentTimeMillis();
            if (time > 0) {
                sendPacket = true;
            }
        } else {
            sendPacket = true;
        }
        map.put(string, System.currentTimeMillis());
        return sendPacket;
    }
}
