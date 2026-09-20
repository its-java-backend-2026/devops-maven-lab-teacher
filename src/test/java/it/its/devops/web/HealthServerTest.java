package it.its.devops.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HealthServerTest {

    @Test
    void usaLaPortaDiDefaultSeNonImpostata() {
        assertEquals(8080, HealthServer.parsePort(null));
        assertEquals(8080, HealthServer.parsePort("  "));
    }

    @Test
    void leggeLaPortaIndicata() {
        assertEquals(3000, HealthServer.parsePort("3000"));
        assertEquals(3000, HealthServer.parsePort(" 3000 "));
    }

    @Test
    void rifiutaUnValorePortaNonNumerico() {
        assertThrows(IllegalArgumentException.class, () -> HealthServer.parsePort("abc"));
    }
}
