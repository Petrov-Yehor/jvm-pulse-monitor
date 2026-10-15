package dev.egor.jpulse;
import com.sun.net.httpserver.HttpServer;
import dev.egor.jpulse.api.HealthHandler;

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
        System.out.println("J-Pulse running on http://localhost:8080");
    }
}