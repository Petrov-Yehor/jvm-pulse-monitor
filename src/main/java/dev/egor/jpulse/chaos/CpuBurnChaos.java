package dev.egor.jpulse.chaos;

import java.util.ArrayList;
import java.util.List;

public class CpuBurnChaos implements Runnable {

    private static final long DURATION_MS = 30_000;

    private final int threadCount = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);

    private final List<Thread> threads = new ArrayList<>();
    private volatile boolean running = false;
    private volatile long deadline = 0;

    private volatile double sink;

    public synchronized void start() {
        if (isAnyThreadAlive()) {
            return;
        }
        running = true;
        deadline = System.currentTimeMillis() + DURATION_MS;
        threads.clear();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(this, "chaos-cpu-burn-" + i);
            thread.setDaemon(true);
            threads.add(thread);
            thread.start();
        }
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running && System.currentTimeMillis() < deadline;
    }

    private boolean isAnyThreadAlive() {
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        double x = 0;
        while (running && System.currentTimeMillis() < deadline) {
            x = x + Math.sqrt(x + 1);
        }
        sink = x;
    }
}