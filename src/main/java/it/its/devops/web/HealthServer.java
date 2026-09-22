package it.its.devops.web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
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
        HttpServer server = createServer(port);
        server.start();
        System.out.println("Servizio in ascolto sulla porta " + port);
    }

    public static HttpServer createServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", HealthServer::handleHealth);
        server.createContext("/hello", HealthServer::handleHello);
        server.createContext("/error", HealthServer::handleError);
        server.createContext("/", HealthServer::handleRoot);
        server.setExecutor(null);
        return server;
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
        try {
            byte[] body = "{ \"status\": \"UP\" }".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Errore interno del server");
        }
    }

    static void handleHello(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            String name = parseQueryParam(query, "nome");
            if (name == null || name.isBlank()) {
                name = "World";
            }
            String jsonResponse = String.format("{ \"message\": \"Hello %s\" }", escapeJson(name));
            byte[] body = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Errore durante l'elaborazione del saluto");
        }
    }

    static void handleError(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        sendErrorResponse(exchange, 500, "Unexpected Error (type=Internal Server Error, status=500)");
    }

    static String parseQueryParam(String query, String paramName) {
        if (query == null || query.isBlank()) {
            return null;
        }
        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            if (pair.length > 0 && pair[0].equals(paramName)) {
                if (pair.length <= 1) {
                    return "";
                }
                try {
                    return java.net.URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                } catch (IllegalArgumentException e) {
                    return pair[1];
                }
            }
        }
        return null;
    }

    private static String escapeJson(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    static void handleRoot(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (!"/".equals(path) && !"/index.html".equals(path)) {
                sendErrorResponse(exchange, 404, "Pagina non trovata: " + path);
                return;
            }
            try (InputStream is = getIndexHtmlStream()) {
                if (is == null) {
                    sendErrorResponse(exchange, 404, "File index.html non trovato nel classpath");
                    return;
                }
                byte[] body = is.readAllBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(body);
                }
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Errore durante il caricamento della pagina web");
        }
    }

    private static InputStream getIndexHtmlStream() {
        InputStream is = HealthServer.class.getResourceAsStream("/index.html");
        if (is == null) {
            is = HealthServer.class.getClassLoader().getResourceAsStream("index.html");
        }
        if (is == null && Thread.currentThread().getContextClassLoader() != null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("index.html");
        }
        return is;
    }

    private static void sendErrorResponse(com.sun.net.httpserver.HttpExchange exchange, int statusCode, String message) throws IOException {
        String json = String.format("{ \"status\": %d, \"error\": \"%s\" }", statusCode, escapeJson(message));
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }
}
