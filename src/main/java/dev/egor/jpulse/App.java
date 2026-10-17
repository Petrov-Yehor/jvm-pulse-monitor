package dev.egor.jpulse;

import com.sun.net.httpserver.HttpServer;
import dev.egor.jpulse.api.HealthHandler;
import dev.egor.jpulse.api.MetricsHandler;
import dev.egor.jpulse.metrics.MetricsCollector;
import dev.egor.jpulse.api.StaticFileHandler;   

import java.io.IOException;
import java.net.InetSocketAddress;

import dev.egor.jpulse.api.ChaosHandler;
import dev.egor.jpulse.chaos.MemoryLeakChaos;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("J-Pulse starting...");

        MetricsCollector collector = new MetricsCollector();
        collector.start();
        MemoryLeakChaos memoryLeak = new MemoryLeakChaos();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/metrics", new MetricsHandler(collector.getHistory()));
        server.createContext("/api/chaos", new ChaosHandler(memoryLeak));
        server.start();

        System.out.println("J-Pulse running on http://localhost:8080");
    }
}