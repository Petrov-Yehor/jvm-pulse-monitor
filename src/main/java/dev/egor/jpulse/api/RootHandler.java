package dev.egor.jpulse.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RootHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        byte[] body = "Hello from J-Pulse".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(200, body.length);
        OutputStream out = exchange.getResponseBody();
        try {
            out.write(body);
        } finally {
            out.close();
        }
    }
}