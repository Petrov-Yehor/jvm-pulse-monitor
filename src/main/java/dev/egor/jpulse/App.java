package dev.egor.jpulse;

import com.sun.net.httpserver.HttpServer;
import dev.egor.jpulse.api.HealthHandler;
import dev.egor.jpulse.api.RootHandler;
import dev.egor.jpulse.metrics.JvmMetricsReader;
import dev.egor.jpulse.metrics.MetricSnapshot;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("J-Pulse starting...");

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/api/health", new HealthHandler());
        server.start();
        System.out.println("J-Pulse running on http://localhost:8080");

        JvmMetricsReader reader = new JvmMetricsReader();
        MetricSnapshot s = reader.read();
        System.out.printf("Heap: %d / %d MB | CPU: %.1f%% | Threads: %d | GC: %d%n", s.heapUsedMb(), s.heapMaxMb(), s.cpuPercent(), s.threadCount(), s.gcCount());
    }
}