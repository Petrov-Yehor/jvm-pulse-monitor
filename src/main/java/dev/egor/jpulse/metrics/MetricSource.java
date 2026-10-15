package dev.egor.jpulse.metrics;

public interface MetricSource {
    MetricSnapshot read();
}