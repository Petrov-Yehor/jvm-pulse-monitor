package dev.egor.jpulse;

import com.sun.net.httpserver.HttpServer;
import dev.egor.jpulse.api.HealthHandler;
import dev.egor.jpulse.api.RootHandler;
import dev.egor.jpulse.metrics.MetricsCollector;

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

        MetricsCollector collector = new MetricsCollector();
        collector.start();
    }
}