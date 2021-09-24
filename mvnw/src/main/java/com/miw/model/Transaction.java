package com.miw.model;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {

    private int transactionId;
    private LocalDateTime transactionDate;
    private int buyer;
    private int seller;
    private Crypto crypto;
    private double units;
    private double transactionPrice;
    private double bankCosts;

    public Transaction(int transactionId, double units, int buyer, int seller, Crypto crypto, double transactionPrice, double bankCosts, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.buyer = buyer;
        this.seller = seller;
        this.crypto = crypto;
        this.units = units;
        this.transactionPrice = transactionPrice;
        this.bankCosts = bankCosts;
        this.transactionDate = transactionDate;
    }

    // price based on units
    public Transaction(int buyer, int seller, Crypto crypto, double units) {
        this.buyer = buyer;
        this.seller = seller;
        this.crypto = crypto;
        this.units = units;
        this.transactionPrice = calculatePrice();
        this.transactionDate = LocalDateTime.now();
    }

    // units based on price
    public Transaction(int buyer, int seller, Crypto crypto, double transactionPrice, double bankCosts) {
        this.buyer = buyer;
        this.seller = seller;
        this.crypto = crypto;
        this.transactionPrice = transactionPrice;
        this.bankCosts = bankCosts;
        this.units = calculateUnits(transactionPrice);
        this.transactionDate = LocalDateTime.now();
    }

    public Transaction(){
        this.transactionDate = LocalDateTime.now();
    }


    private double calculatePrice() {
        return units * crypto.getCryptoPrice();
    }

    private double calculateUnits(double transactionPrice) {
        return transactionPrice / crypto.getCryptoPrice();
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public int getBuyer() {
        return buyer;
    }

    public int getSeller() {
        return seller;
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public double getUnits() {
        return units;
    }

    public double getTransactionPrice() {
        return transactionPrice;
    }

    public void setTransactionPrice(double transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

    public double getBankCosts() {
        return bankCosts;
    }

    public void setBankCosts(double bankCosts) {
        this.bankCosts = bankCosts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}
