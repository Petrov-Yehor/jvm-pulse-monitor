package dev.egor.jpulse.metrics;

public class MetricsCollector implements Runnable {

    private final JvmMetricsReader reader = new JvmMetricsReader();

    private volatile MetricSnapshot latest;

    public void start() {
        Thread thread = new Thread(this, "metrics-collector");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                MetricSnapshot s = reader.read();
                latest = s;
                System.out.printf("Heap: %d / %d MB | CPU: %.1f%% | Threads: %d | GC: %d%n",
                    s.heapUsedMb(), s.heapMaxMb(), s.cpuPercent(), s.threadCount(), s.gcCount());
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

    public MetricSnapshot getLatest() {
        return latest;
    }
}