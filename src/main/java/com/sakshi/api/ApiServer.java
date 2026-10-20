package com.sakshi.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakshi.model.UserStory;
import com.sakshi.service.GenerationResult;
import com.sakshi.service.GenerationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executors;

/** Small local bridge for the React dashboard and the existing Java generator. */
public final class ApiServer {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ApiServer() {
    }

    public static void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        server.createContext("/api/health", exchange -> send(exchange, 200, Map.of("status", "ok")));
        server.createContext("/api/generate", ApiServer::generate);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Java generator API listening at http://localhost:" + port);
    }

    private static void generate(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) { send(exchange, 204, Map.of()); return; }
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) { send(exchange, 405, Map.of("error", "POST is required")); return; }

        try {
            Map<String, Object> body = MAPPER.readValue(exchange.getRequestBody(), new TypeReference<>() {});
            UserStory story = new UserStory(text(body, "feature"), text(body, "userStory"), text(body, "expectedBehavior"));
            GenerationResult result = GenerationService.generate(story);
            if (!result.success()) { send(exchange, 400, Map.of("error", result.message())); return; }
            send(exchange, 200, Map.of("testCases", result.response().getTestCases(), "message", result.message()));
        } catch (Exception ex) {
            send(exchange, 400, Map.of("error", ex.getMessage() == null ? "Invalid generation request" : ex.getMessage()));
        }
    }

    private static String text(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    private static void send(HttpExchange exchange, int status, Object payload) throws IOException {
        byte[] bytes = MAPPER.writeValueAsBytes(payload);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var output = exchange.getResponseBody()) { output.write(bytes); }
    }
}
