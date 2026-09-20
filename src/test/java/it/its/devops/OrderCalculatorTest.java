package it.its.devops;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderCalculatorTest {

    private final OrderCalculator calculator = new OrderCalculator();

    @Test
    void calcolaIlTotale() {
        var result = calculator.total(new BigDecimal("12.50"), 4);

        assertEquals(new BigDecimal("50.00"), result);
    }

    @Test
    void rifiutaUnaQuantitaNonPositiva() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.total(new BigDecimal("10.00"), 0));
    }

    @Test
    void rifiutaUnPrezzoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.total(new BigDecimal("-1.00"), 1));
    }
}

