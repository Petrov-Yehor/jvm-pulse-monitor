package dev.egor.jpulse.chaos;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;

public class MemoryLeakChaos implements Runnable {

    private static final int CHUNK_BYTES = 5 * 1024 * 1024;

    private static final double MAX_HEAP_FRACTION = 0.85;

    private final List<byte[]> leaked = new ArrayList<>();

    private volatile boolean running = false;
    private Thread thread;

    public synchronized void start() {
        if (thread != null && thread.isAlive()) {
            return;
        }
        running = true;
        thread = new Thread(this, "chaos-memory-leak");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        while (running) {
            MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            long limit = (long) (heap.getMax() * MAX_HEAP_FRACTION);

            if (heap.getUsed() + CHUNK_BYTES < limit) {
                leaked.add(new byte[CHUNK_BYTES]);
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                break;
            }
        }
        leaked.clear();
    }
}