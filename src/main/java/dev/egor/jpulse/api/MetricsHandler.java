package dev.egor.jpulse.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.egor.jpulse.metrics.MetricSnapshot;
import dev.egor.jpulse.metrics.MetricsHistory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MetricsHandler implements HttpHandler {

    private final MetricsHistory history;

    public MetricsHandler(MetricsHistory history) {
        this.history = history;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        List<MetricSnapshot> snapshots = history.getAll();

        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < snapshots.size(); i++) {
            MetricSnapshot s = snapshots.get(i);

            if (i > 0) {
                json.append(",");
            }

            double cpu = Math.round(s.cpuPercent() * 10) / 10.0;

            json.append("{");
            json.append("\"timestamp\":").append(s.timestamp()).append(",");
            json.append("\"heapUsedMb\":").append(s.heapUsedMb()).append(",");
            json.append("\"heapMaxMb\":").append(s.heapMaxMb()).append(",");
            json.append("\"cpuPercent\":").append(cpu).append(",");
            json.append("\"threadCount\":").append(s.threadCount()).append(",");
            json.append("\"gcCount\":").append(s.gcCount());
            json.append("}");
        }
        json.append("]");

        byte[] body = json.toString().getBytes(StandardCharsets.UTF_8);
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