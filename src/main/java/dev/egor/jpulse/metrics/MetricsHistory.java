package dev.egor.jpulse.metrics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MetricsHistory {

    private static final int MAX_SIZE = 300;

    private final LinkedList<MetricSnapshot> snapshots = new LinkedList<>();

    public synchronized void add(MetricSnapshot snapshot) {
        snapshots.addLast(snapshot);
        if (snapshots.size() > MAX_SIZE) {
            snapshots.removeFirst();
        }
    }

    public synchronized List<MetricSnapshot> getAll() {
        return new ArrayList<>(snapshots);
    }

    public synchronized MetricSnapshot getLatest() {
        return snapshots.peekLast();
    }

    public synchronized int size() {
        return snapshots.size();
    }
}