package it.its.devops;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        var calculator = new OrderCalculator();
        var result = calculator.total(new BigDecimal("12.50"), 4);
        System.out.println("Totale ordine: €" + result);
    }
}

