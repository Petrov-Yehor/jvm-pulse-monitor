package dev.egor.jpulse.metrics;

public record MetricSnapshot(
    long timestamp,     
    long heapUsedMb,    
    long heapMaxMb,     
    double cpuPercent,  
    int threadCount,    
    long gcCount        
) {
}