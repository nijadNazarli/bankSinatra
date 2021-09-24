package com.miw.model;

import org.iban4j.CountryCode;
import org.iban4j.Iban;
import java.util.Objects;

public class Account {

    private int accountId;
    private String iban;
    private double balance;
    private final String BANKCODE = "BSIN";

    public Account() {
        this(10000);
    }

    public Account(double balance) {
        this.iban = new Iban.Builder().countryCode(CountryCode.NL).bankCode(BANKCODE).buildRandom().toString();
        this.balance = balance;
    }

    public Account(int accountId, String iban, double balance) {
        this.accountId = accountId;
        this.iban = iban;
        this.balance = balance;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }
}
