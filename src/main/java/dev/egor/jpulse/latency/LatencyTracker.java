package dev.egor.jpulse.latency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LatencyTracker {

    private final List<Long> samplesNanos = new ArrayList<>();

    public synchronized void record(long nanos) {
        samplesNanos.add(nanos);
    }

    public synchronized LatencyStats takeStats() {
        if (samplesNanos.isEmpty()) {
            return new LatencyStats(0, 0);
        }
        List<Long> sorted = new ArrayList<>(samplesNanos);
        samplesNanos.clear();
        Collections.sort(sorted);
        return new LatencyStats(percentileMs(sorted, 0.50), percentileMs(sorted, 0.95));
    }

    private double percentileMs(List<Long> sorted, double p) {
        int index = (int) Math.ceil(p * sorted.size()) - 1;
        return sorted.get(index) / 1_000_000.0;
    }
}