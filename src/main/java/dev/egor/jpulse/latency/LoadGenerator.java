package dev.egor.jpulse.latency;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class LoadGenerator implements Runnable {

    private final LatencyTracker tracker;
    private final HttpClient client = HttpClient.newHttpClient();
    private final HttpRequest request;

    public LoadGenerator(LatencyTracker tracker, int port) {
        this.tracker = tracker;
        this.request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/work"))
            .timeout(Duration.ofSeconds(5))
            .GET()
            .build();
    }

    public void start() {
        Thread thread = new Thread(this, "load-generator");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                long start = System.nanoTime();
                client.send(request, HttpResponse.BodyHandlers.discarding());
                tracker.record(System.nanoTime() - start);
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}