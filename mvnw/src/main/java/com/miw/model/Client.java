package com.miw.model;

import com.miw.service.authentication.ElevenCheck;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Client extends User{

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @ElevenCheck
    private int bsn;

    @Valid
    private Address address;
    private Map<Crypto, Double> portfolio;
    private Account account;

    public Client(String email, String password, String salt, String firstName, String prefix, String lastName, LocalDate dateOfBirth, int bsn, Address address) {
        super(email, password, salt, firstName, prefix, lastName);
        this.dateOfBirth = dateOfBirth;
        this.bsn = bsn;
        this.address = address;
        this.portfolio = new HashMap<>();       // TODO: besluiten wat voor soort Map we gaan gebruiken
    }

    public Client(String email, String password) {
        super(email, password);
    }

    public Client() {
        this.account = new Account();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getBsn() {
        return bsn;
    }

    public void setBsn(int bsn) {
        this.bsn = bsn;
    }

    public Address getAddress() {
        return address;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Client{" +
                "account=" + account +
                ", userId=" + userId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
