package brain_architecture.kernel;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * Minimal fake llama.cpp-compatible HTTP server for adapter tests.
 */
public final class FakeDockerLlamaServerMain {
    private FakeDockerLlamaServerMain() {
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 0;
        for (int index = 0; index < args.length - 1; index++) {
            if ("--host".equals(args[index])) {
                host = args[index + 1];
            } else if ("--port".equals(args[index])) {
                port = Integer.parseInt(args[index + 1]);
            }
        }
        if (port <= 0) {
            throw new IllegalArgumentException("port required");
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/health", new StaticHandler("ok"));
        server.createContext("/completion", new StaticHandler("{\"content\":\"docker fake native output\"}"));
        server.start();
        Thread.sleep(Long.MAX_VALUE);
    }

    private static final class StaticHandler implements HttpHandler {
        private final byte[] payload;

        private StaticHandler(String payload) {
            this.payload = payload.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, payload.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(payload);
            }
        }
    }
}
