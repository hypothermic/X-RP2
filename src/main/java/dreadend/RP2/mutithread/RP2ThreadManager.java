/* X-RP - decompiled with CFR */
package dreadend.RP2.mutithread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RP2ThreadManager {
    public static LinkedBlockingQueue tubeThreadQueue = new LinkedBlockingQueue();
    public static ExecutorService tubeThread = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, tubeThreadQueue);
}

