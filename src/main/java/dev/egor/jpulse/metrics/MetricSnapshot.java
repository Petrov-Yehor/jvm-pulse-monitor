package dev.egor.jpulse.metrics;

public record MetricSnapshot(
    long timestamp,     
    long heapUsed,      
    long heapMax,      
    double processCpu,  
    int threadCount,    
    long gcCount,       
    long gcTimeMs       
) {
}