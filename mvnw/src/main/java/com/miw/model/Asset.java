package com.miw.model;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Asset {

    private int accountId;
    public Crypto crypto;
    private double units;
    private double currentValue;
    private double unitsForSale;
    private double salePrice;
    private static final double DEFAULT_UNITS = 0;
    private Map<String, Double> historicalNrOfUnits; //time interval, units
    private Map<String, Double> historicalValues; //time interval, oldValue
    private Map<String, Double> deltaValues;

    public Asset(Crypto crypto, double units, double unitsForSale, double salePrice) {
        super();
        this.crypto = crypto;
        this.units = units;
        this.currentValue = calculateValue();
        this.historicalValues = new TreeMap<>();
        this.historicalNrOfUnits = new TreeMap<>();
        this.deltaValues = new TreeMap<>();
        this.unitsForSale = unitsForSale;
        this.salePrice = salePrice;
    }

    public Asset(Crypto crypto, double units) {
        this(crypto, units, DEFAULT_UNITS, DEFAULT_UNITS);
    }

    public Asset(){}

    private double calculateValue() {
        return units * crypto.getCryptoPrice();
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public void setCrypto(Crypto crypto) {
        this.crypto = crypto;
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public Map<String, Double> getHistoricalValues() {
        return historicalValues;
    }

    public Map<String, Double> getHistoricalNrOfUnits() {
        return historicalNrOfUnits;
    }

    public Map<String, Double> getDeltaValues() {
        return deltaValues;
    }

    public double getUnitsForSale() {
        return unitsForSale;
    }

    public double getSalePrice() {
        return salePrice;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "accountId=" + accountId +
                ", crypto=" + crypto +
                ", units=" + units +
                ", currentValue=" + currentValue +
                ", unitsForSale=" + unitsForSale +
                ", salePrice=" + salePrice +
                '}';
    }


    public static class CurrentValueComparator implements Comparator<Asset> {
        //Sorteren op assetwaarde van groot naar klein
        @Override
        public int compare(Asset o1, Asset o2) {
            return (int) (o2.getCurrentValue() - o1.getCurrentValue());
        }
    }

}
