package dev.egor.jpulse.metrics;

public record MetricSnapshot(
    long timestamp,     
    long heapUsedMb,    
    long heapMaxMb,     
    double cpuPercent,  
    int threadCount,
    long gcCount,
    double latencyP50Ms,
    double latencyP95Ms        
) {
}