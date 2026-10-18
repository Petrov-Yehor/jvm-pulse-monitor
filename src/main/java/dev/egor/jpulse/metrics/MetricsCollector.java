package dev.egor.jpulse.metrics;

import dev.egor.jpulse.latency.LatencyTracker;

public class MetricsCollector implements Runnable {

    private final JvmMetricsReader reader = new JvmMetricsReader();
    private final MetricsHistory history = new MetricsHistory();
    private final LatencyTracker latencyTracker;

    public MetricsCollector(LatencyTracker latencyTracker) {
        this.latencyTracker = latencyTracker;
    }

    public void start() {
        Thread thread = new Thread(this, "metrics-collector");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                MetricSnapshot s = reader.read(latencyTracker.takeStats());
                history.add(s);
                System.out.printf("Heap: %d / %d MB | CPU: %.1f%% | Threads: %d | GC: %d | p95: %.1f ms | History: %d%n", s.heapUsedMb(), s.heapMaxMb(), s.cpuPercent(), s.threadCount(), s.gcCount(), s.latencyP95Ms(), history.size());
            } catch (Exception e) {
                System.err.println("Failed to collect metrics: " + e.getMessage());
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public MetricsHistory getHistory() {
        return history;
    }
}