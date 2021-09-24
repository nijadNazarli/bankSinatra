package com.miw.model;

public class Bank {
    private static Bank bankSinatra = null;
    public static final int BANK_ID = 1;
    private Account account;
    private final double START_KAPITAAL = 5000000;
    private final String BANK_IBAN = "NL91BSIN9826496343";

    private Bank() {
        this.account = new Account(BANK_ID, BANK_IBAN, START_KAPITAAL);
    }

    @Override
    public String toString() {
        return "Bank{" +
                "Account number: " + BANK_ID + " }";
    }

    public static Bank getBankSinatra() {
        if (bankSinatra == null) {
            bankSinatra = new Bank();
        }
        return bankSinatra;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
