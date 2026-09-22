package it.its.devops.web;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthServerIntegrationTest {

    private HttpServer server;
    private int port;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        // Avvia il server su una porta effimera disponibile (0)
        server = HealthServer.createServer(0);
        server.start();
        port = server.getAddress().getPort();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void testHelloEndpointConNome() throws Exception {
        URI uri = URI.create("http://localhost:" + port + "/hello?nome=Mario");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("application/json", response.headers().firstValue("Content-Type").orElse(null));
        assertEquals("{ \"message\": \"Hello Mario\" }", response.body());
    }

    @Test
    void testHelloEndpointSenzaNomeDefault() throws Exception {
        URI uri = URI.create("http://localhost:" + port + "/hello");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("application/json", response.headers().firstValue("Content-Type").orElse(null));
        assertEquals("{ \"message\": \"Hello World\" }", response.body());
    }

    @Test
    void testHealthEndpoint() throws Exception {
        URI uri = URI.create("http://localhost:" + port + "/health");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("application/json", response.headers().firstValue("Content-Type").orElse(null));
        assertEquals("{ \"status\": \"UP\" }", response.body());
    }

    @Test
    void testRootHtmlEndpoint() throws Exception {
        URI uri = URI.create("http://localhost:" + port + "/");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("text/html; charset=utf-8", response.headers().firstValue("Content-Type").orElse(null));
        org.junit.jupiter.api.Assertions.assertTrue(response.body().contains("Greeting Service"));
    }

    @Test
    void testErrorEndpoint() throws Exception {
        URI uri = URI.create("http://localhost:" + port + "/error");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
        assertEquals("application/json", response.headers().firstValue("Content-Type").orElse(null));
        org.junit.jupiter.api.Assertions.assertTrue(response.body().contains("Internal Server Error"));
    }

    @Test
    void testNotFoundEndpoint() throws Exception {
        URI uri = URI.create("http://localhost:" + port + "/non-esistente");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("application/json", response.headers().firstValue("Content-Type").orElse(null));
        org.junit.jupiter.api.Assertions.assertTrue(response.body().contains("Pagina non trovata"));
    }
}
