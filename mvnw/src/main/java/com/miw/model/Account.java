package com.miw.model;

public class Account {

    private int accountId;
    private String iban;
    private double balance;

    public Account() {
        this.iban = generateIban();
        this.balance = 10000.00;
    }

    public Account(double balance) {
        this.iban = generateIban();
        this.balance = balance;
    }


    private String generateIban() {
        //TODO: create unique iban number
        return "generatedIban";
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", iban='" + iban + '\'' +
                ", balance=" + balance +
                '}';
    }
}
