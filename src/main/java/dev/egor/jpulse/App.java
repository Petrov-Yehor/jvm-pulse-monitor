package dev.egor.jpulse;
import com.sun.net.httpserver.HttpServer;
import dev.egor.jpulse.api.HealthHandler;
import dev.egor.jpulse.metrics.MetricSnapshot;
import dev.egor.jpulse.metrics.MetricSource;
import dev.egor.jpulse.metrics.LocalJvmSource;
    
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;


public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("J-Pulse starting...");
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> {
            byte[] body = "Hello from J-Pulse".getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        
        server.createContext("/api/health", new HealthHandler());
        server.start();
        MetricSource source = new LocalJvmSource();
        MetricSnapshot s = source.read();
        System.out.printf("Heap: %d / %d MB | CPU: %.1f%% | Threads: %d | GC: %d (%d ms)%n",
            s.heapUsed() / 1024 / 1024,
            s.heapMax() / 1024 / 1024,
            s.processCpu() * 100,
            s.threadCount(),
            s.gcCount(),
            s.gcTimeMs());
        System.out.println("J-Pulse running on http://localhost:8080");
    }
}