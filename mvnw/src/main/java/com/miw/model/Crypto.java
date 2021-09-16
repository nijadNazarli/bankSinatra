package com.miw.model;

public class Crypto {

    // ATTRIBUTES
    private String name;
    private String symbol;
    private String description;
    private double exchangeRate;


    // CONSTRUCTORS
    public Crypto(String name, String symbol, String description) {
        this.name = name;
        this.symbol = symbol;
        this.description = description;
        this.exchangeRate = retrieveValue();
    }

    // METHODS
    private double retrieveValue() {
        //TODO: get recent value of cryptocoin thru API?
        return 0.0;
    }

    // GETTERS & SETTERS
    public double getValue() {
        return exchangeRate;
    }
}
