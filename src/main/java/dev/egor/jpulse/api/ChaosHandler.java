package dev.egor.jpulse.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.egor.jpulse.chaos.MemoryLeakChaos;
import dev.egor.jpulse.chaos.CpuBurnChaos;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ChaosHandler implements HttpHandler {

    private final MemoryLeakChaos memoryLeak;
    private final CpuBurnChaos cpuBurn;

    public ChaosHandler(MemoryLeakChaos memoryLeak, CpuBurnChaos cpuBurn) {
        this.memoryLeak = memoryLeak;
        this.cpuBurn = cpuBurn;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (method.equals("GET") && path.equals("/api/chaos/status")) {
            sendState(exchange);
            return;
        }

        if (!method.equals("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        if (path.equals("/api/chaos/leak/start")) {
            memoryLeak.start();
        } else if (path.equals("/api/chaos/leak/stop")) {
            memoryLeak.stop();
        } else if (path.equals("/api/chaos/cpu/start")) {
            cpuBurn.start();
        } else if (path.equals("/api/chaos/cpu/stop")) {
            cpuBurn.stop();
        } else if (path.equals("/api/chaos/gc")) {
            System.gc();
        } else {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        sendState(exchange);
    }

    private void sendState(HttpExchange exchange) throws IOException {
        String json = "{\"leak\":" + memoryLeak.isRunning() + ",\"cpu\":" + cpuBurn.isRunning() + "}";
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