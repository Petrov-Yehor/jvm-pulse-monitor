package dev.egor.jpulse.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.egor.jpulse.chaos.MemoryLeakChaos;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ChaosHandler implements HttpHandler {

    private final MemoryLeakChaos memoryLeak;

    public ChaosHandler(MemoryLeakChaos memoryLeak) {
        this.memoryLeak = memoryLeak;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();

        if (path.equals("/api/chaos/leak/start")) {
            memoryLeak.start();
        } else if (path.equals("/api/chaos/leak/stop")) {
            memoryLeak.stop();
        } else if (path.equals("/api/chaos/gc")) {
            System.gc();
        } else {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        String json = "{\"leak\":" + memoryLeak.isRunning() + "}";
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(200, body.length);
        OutputStream out = exchange.getResponseBody();
        try {
            out.write(body);
        } finally {
            out.close();
        }
    }
}