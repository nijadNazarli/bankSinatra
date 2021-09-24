package com.miw.model;

import java.util.Objects;

public class Crypto implements Comparable<Crypto>{

    private String name;
    private String symbol;
    private String description;
    private double cryptoPrice;

    public Crypto(String name, String symbol, String description, Double cryptoPrice) {
        this.name = name;
        this.symbol = symbol;
        this.description = description;
        this.cryptoPrice = cryptoPrice;
    }

    public Crypto(){}

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getCryptoPrice() {
        return cryptoPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crypto crypto = (Crypto) o;
        return symbol.equals(crypto.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Crypto{" +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", description='" + description + '\'' +
                ", cryptoPrice=" + cryptoPrice +
                '}';
    }

    @Override
    public int compareTo(Crypto o) {
        return this.name.compareTo(o.getName());
    }
}
