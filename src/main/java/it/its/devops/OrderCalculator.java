package it.its.devops;

import java.math.BigDecimal;

/** Calcola il totale di un ordine. È il punto di partenza dell'esercizio. */
public class OrderCalculator {

    public BigDecimal total(BigDecimal unitPrice, int quantity) {
        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Il prezzo deve essere non negativo");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantità deve essere positiva");
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

