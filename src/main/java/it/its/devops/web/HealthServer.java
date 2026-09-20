package it.its.devops.web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Trasforma il programma da console in un servizio HTTP minimo, come richiesto
 * per il deploy (LAB 3): resta in ascolto sulla porta indicata da {@code PORT}
 * ed espone {@code GET /health}. Nessuna libreria esterna: usa solo le classi
 * incluse nel JDK, per restare un esempio semplice da leggere in una lezione
 * di DevOps, non di sviluppo web.
 */
public class HealthServer {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws IOException {
        int port = readPort();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", HealthServer::handleHealth);
        server.setExecutor(null);
        server.start();
        System.out.println("Servizio in ascolto sulla porta " + port);
    }

    static int readPort() {
        return parsePort(System.getenv("PORT"));
    }

    /** Separata da readPort() per poter essere testata senza toccare le variabili d'ambiente. */
    static int parsePort(String value) {
        if (value == null || value.isBlank()) {
            return DEFAULT_PORT;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("PORT non è un numero valido: " + value, e);
        }
    }

    private static void handleHealth(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        byte[] body = "{ \"status\": \"UP\" }".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }
}
