package miw.model;

import java.util.Objects;

public class Crypto implements Comparable<com.miw.model.Crypto>{

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

    private double retrieveValue() {
        //TODO: get recent value of cryptocoin thru API?
        return 0.0;
    }


    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getCryptoPrice() {
        return cryptoPrice;
    }

    public void setCryptoPrice(double cryptoPrice) {
        this.cryptoPrice = cryptoPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.miw.model.Crypto crypto = (com.miw.model.Crypto) o;
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
    public int compareTo(com.miw.model.Crypto o) {
        return this.name.compareTo(o.getName());
    }
}
