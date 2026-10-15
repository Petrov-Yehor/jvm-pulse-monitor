package dev.egor.jpulse.metrics;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.List;

public class LocalJvmSource implements MetricSource {
    
    private final MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
    private final ThreadMXBean threads = ManagementFactory.getThreadMXBean();
    private final List<GarbageCollectorMXBean> gcs = ManagementFactory.getGarbageCollectorMXBeans();
    
    private final com.sun.management.OperatingSystemMXBean os =
        (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    @Override
    public MetricSnapshot read() {
        MemoryUsage heap = memory.getHeapMemoryUsage();
        
        long gcCount = 0;
        long gcTimeMs = 0;
        for (GarbageCollectorMXBean gc : gcs) {
            if (gc.getCollectionCount() > 0) gcCount += gc.getCollectionCount();
            if (gc.getCollectionTime() > 0) gcTimeMs += gc.getCollectionTime();
        }

        return new MetricSnapshot(
            System.currentTimeMillis(),
            heap.getUsed(),
            heap.getMax(),
            os.getProcessCpuLoad(),
            threads.getThreadCount(),
            gcCount,
            gcTimeMs
        );
    }
}