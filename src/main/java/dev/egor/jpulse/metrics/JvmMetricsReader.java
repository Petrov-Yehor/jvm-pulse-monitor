package dev.egor.jpulse.metrics;

import com.sun.management.OperatingSystemMXBean;
import dev.egor.jpulse.latency.LatencyStats;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

public class JvmMetricsReader {

    public MetricSnapshot read(LatencyStats latency) {
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        long heapUsedMb = heap.getUsed() / 1024 / 1024;
        long heapMaxMb = heap.getMax() / 1024 / 1024;

        OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuPercent = os.getProcessCpuLoad() * 100;
        if (cpuPercent < 0) {
            cpuPercent = 0;
        }

        int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();

        long gcCount = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (gc.getCollectionCount() > 0) {
                gcCount = gcCount + gc.getCollectionCount();
            }
        }

        return new MetricSnapshot(System.currentTimeMillis(), heapUsedMb, heapMaxMb, cpuPercent, threadCount, gcCount, latency.p50Ms(), latency.p95Ms());
    }
}