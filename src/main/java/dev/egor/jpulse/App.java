package dev.egor.jpulse;

import com.sun.net.httpserver.HttpServer;
import dev.egor.jpulse.api.ChaosHandler;
import dev.egor.jpulse.api.HealthHandler;
import dev.egor.jpulse.api.MetricsHandler;
import dev.egor.jpulse.api.StaticFileHandler;
import dev.egor.jpulse.chaos.CpuBurnChaos;
import dev.egor.jpulse.chaos.MemoryLeakChaos;
import dev.egor.jpulse.latency.LatencyTracker;
import dev.egor.jpulse.metrics.MetricsCollector;
import dev.egor.jpulse.api.WorkHandler;
import dev.egor.jpulse.latency.LoadGenerator;
import java.io.IOException;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("J-Pulse starting...");

        LatencyTracker latencyTracker = new LatencyTracker();
        MetricsCollector collector = new MetricsCollector(latencyTracker);
        collector.start();
        MemoryLeakChaos memoryLeak = new MemoryLeakChaos();
        CpuBurnChaos cpuBurn = new CpuBurnChaos();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/metrics", new MetricsHandler(collector.getHistory()));
        server.createContext("/api/chaos", new ChaosHandler(memoryLeak, cpuBurn));
        server.createContext("/api/work", new WorkHandler());
        server.start();

        LoadGenerator loadGenerator = new LoadGenerator(latencyTracker, 8080);
        loadGenerator.start();
        System.out.println("J-Pulse running on http://localhost:8080");
    }
}