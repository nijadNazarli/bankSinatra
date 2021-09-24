package com.miw.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Objects;

public class Address {

    @NotEmpty
    @Pattern(regexp="^([a-zA-Z'\\u0080-\\u024F]+(?:. |-| |'))*[a-zA-Z'\\u0080-\\u024F]*$",
            message="Must start with a letter and can only contain letters and spaces afterwards.")
    private String city;

    @NotEmpty
    @Pattern(regexp="^[1-9][0-9]{3} ?(?!sa|sd|ss|SA|SD|SS)[A-Za-z]{2}$",
            message="Only Dutch zip codes are accepted at the moment with format: NNNN AA. " +
                    "For foreign clients, please stay tuned for future foreign service expansions.")
    private String zipCode;

    @NotEmpty
    @Pattern(regexp="^([a-zA-Z.\\u0080-\\u024F]+(?:. |-| |'))*[a-zA-Z0-9.\\u0080-\\u024F]*$",
            message="Must start with a letter and can only contain letters and spaces afterwards.")
    private String street;

    @Min(1)
    private int houseNumber;

    @Pattern(regexp="[a-zA-Z]*[0-9]*-?/?[a-zA-Z]*[0-9]*",
            message="Can only contain letters and numbers with an optional dash or slash inbetween.")
    private String houseNumberExtension;


    public Address(String city, String zipCode, String street, int houseNumber, String houseNumberExtension) {
        this.city = city;
        this.zipCode = zipCode;
        this.street = street;
        this.houseNumber = houseNumber;
        this.houseNumberExtension = houseNumberExtension;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getHouseNumberExtension() {
        return houseNumberExtension;
    }

    public void setHouseNumberExtension(String houseNumberExtension) {
        this.houseNumberExtension = houseNumberExtension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return houseNumber == address.houseNumber && city.equals(address.city) && zipCode.equals(address.zipCode)
                && street.equals(address.street) && Objects.equals(houseNumberExtension, address.houseNumberExtension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, zipCode, street, houseNumber, houseNumberExtension);
    }
}

