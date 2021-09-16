package com.miw.model;


import java.time.LocalDateTime;

public class Transaction {

    // ATTRIBUTES
    private int transactionNumber;
    private LocalDateTime transactionDate;
    private Client buyer;
    private Client seller;
    private Crypto crypto;
    private double units;
    private double price;
    private double bankCosts;


    // CONSTRUCTORS

    // price based on units
    public Transaction(Client buyer, Client seller, Crypto crypto, double units) {
        this.buyer = buyer;
        this.seller = seller;
        this.crypto = crypto;
        this.units = units;
        this.price = calculatePrice();
        this.transactionDate = LocalDateTime.now();
        this.transactionNumber = generateTransactionNumber();
    }


    // units based on price
    public Transaction(Client buyer, Client seller, Crypto crypto, double price, double bankCosts) {
        this.buyer = buyer;
        this.seller = seller;
        this.crypto = crypto;
        this.price = price;
        this.bankCosts = bankCosts;
        this.units = calculateUnits(price);
        this.transactionDate = LocalDateTime.now();
    }



    // METHODS
    private double calculatePrice() {
        //TODO: calculate price based on unit and course value of cryptocoin
        return units * crypto.getValue();
    }

    private double calculateUnits(double price) {
        //TODO: calculate price based on unit and course value of cryptocoin
        return price * crypto.getValue();
    }

    private int generateTransactionNumber() {
        //TODO: generate unique transactionNumber

        return 0;
    }

    // GETTERS & SETTERS
}
