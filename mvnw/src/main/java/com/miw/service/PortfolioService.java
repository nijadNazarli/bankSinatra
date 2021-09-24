/**
 * @Author Johnny Chan
 * @Description This class obtains and calculates the client's portfolio with all assets ever owned,
 * including the current, historical and delta values. The portfolio is returned to the PortfolioController.
 */
package com.miw.service;

import com.miw.database.JdbcCryptoDao;
import com.miw.database.RootRepository;
import com.miw.model.Asset;
import com.miw.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PortfolioService {

    private final RootRepository rootRepository;
    private final JdbcCryptoDao jdbcCryptoDao;
    private Map<String, Asset> portfolioAssets = new TreeMap<>();

    @Autowired
    public PortfolioService(RootRepository rootRepository, JdbcCryptoDao jdbcCryptoDao) {
        this.rootRepository = rootRepository;
        this.jdbcCryptoDao = jdbcCryptoDao;
    }

    public Client findClientByEmail(String email) {
        return rootRepository.findClientByEmail(email);
    }

    public int getAccountIdByUserId(int userId) {
        return rootRepository.getAccountByUserId(userId).getAccountId();
    }

    public double getTotalPortfolioValue(int accountId) {
        List<Asset> assetList = rootRepository.getAssets(accountId);
        double totalPortfolioValue = 0.0;
        for (Asset asset : assetList) {
            totalPortfolioValue += asset.getCurrentValue();
        }
        return totalPortfolioValue;
    }

    public List<Asset> getAssets(int accountId) {
        List<Asset> assetList = rootRepository.getAssets(accountId);
        assetList.sort(new Asset.CurrentValueComparator()); //Sorteer assets van groot naar klein o.b.v. asset's currentValue
        return assetList;
    }

    /**
     * Obtains the client's portfolio with all assets, including the current, historical and delta values.
     * Delta values are changes in the value of the assets and total portfolio
     * compared to 1 day/1 month/3 months/1 year ago/start date.
     *
     * @param accountId client's accountId as in the database
     * @return Client's portfolio with all assets, including the current, historical and delta values.
     */
    // Door map van alle portfolioAssets loopen (global attribute) en hiervan deltawaarden berekenen + totale historische waarden
    public Map<String, Object> getPortfolio(int accountId) {
        Map<String, Object> portfolio = new TreeMap<>();
        calculatePortfolioHistoricalValues(accountId); //vult een global map met Assets. Ieder asset bevat de current, historical en deltawaarden in eigen maps.
        portfolio.put("Portfolio assets", portfolioAssets);
        // De keys (e.g. value1DayAgo) uit de historicalValues map uit een willekeurige asset halen.
        // Bepaalt ook het aantal benodigde puts (i.e. loops) voor de map met totale delta values.
        Asset assets = portfolioAssets.values().stream().findFirst().get();
        List<String> deltaKeys = new ArrayList<>(assets.getHistoricalValues().keySet());
        for (String deltaKey : deltaKeys) {
            double totalCurrentValue = 0.0;
            double totalHistoricalValue = 0.0;
            //loopen door de portfolio-map met assets, hiervan de totale delta values van de gehele portfolio berekenen.
            for (Asset asset : portfolioAssets.values()) {
                totalCurrentValue += asset.getCurrentValue();
                totalHistoricalValue += asset.getHistoricalValues().get(deltaKey);
            }
            portfolio.put(("Total delta " + deltaKey), totalCurrentValue - totalHistoricalValue);
            portfolio.put(("Total delta " + deltaKey + " %"), calculateDeltaPct(totalCurrentValue, totalHistoricalValue));
            portfolio.put("Total  current value: ", totalCurrentValue);
        }
        return portfolio;
    }

    //Haal map van assets op dateTime x op o.b.v. Transaction-history met columns: symbol, sumOfTransactions
    // loop erdoorheen en voor elke asset:
    // -> terugrekenen naar historicalUnits en opslaan in map in Asset
    // -> historicalPrice ophalen
    // -> historical value (p*q) berekenen, deze opslaan in een aparte Map
    // -> voor elke huidige asset de delta value van de asset berekenen, opslaan in map in Asset

    //Result: 2 objecten
    // a. map met Assets (met daarin map met historical units en deltawaarden)
    // b. map met total historical deltawaarden van gehele portfolio

    /**
     * Obtains a list of all crypto-assets ever owned by the user from the database.
     * For each crypto-asset ever owned, calculates the difference between the current value (units * current price)
     * and the value at a given historical dateTime (units on dateTime * price on dateTime).
     * This reflects both changes in market price and volume (units of assets purchased/sold).
     *
     * @param accountId client's accountId as in the database
     */
    private void calculatePortfolioHistoricalValues(int accountId) {
        List<String> allCryptosOwned = rootRepository.getAllCryptosOwned(accountId);
        LocalDateTime dateTime = rootRepository.getDateTimeOfFirstTransaction(accountId); //get startDate = date of first transaction
        for (String symbol : allCryptosOwned) {
            Asset asset = rootRepository.getAssetBySymbol(accountId, symbol);
            asset = calculateValuesNrOfDaysAgo(accountId, asset, symbol, 1);
            asset = calculateValuesNrOfMonthsAgo(accountId, asset, symbol, 1);
            asset = calculateValuesNrOfMonthsAgo(accountId, asset, symbol, 3);
            asset = calculateValuesNrOfMonthsAgo(accountId, asset, symbol, 12);
            asset = calculateValuesAtDate(accountId, asset, symbol, dateTime);
            portfolioAssets.put(symbol, asset); //asset met current, historical en deltawaarden opslaan in global map
        }
    }

    private Asset calculateValuesNrOfDaysAgo(int accountId, Asset asset, String symbol, int days) {
        String xDays = (days == 1) ? "1Day" : (days + "Days");
        double unitsDaysAgo = rootRepository.getSymbolUnitsAtDateTime(accountId, symbol, LocalDateTime.now().minusDays(days));
        asset.getHistoricalNrOfUnits().put(("units" + xDays + "Ago"), unitsDaysAgo);
        double priceDaysAgo = jdbcCryptoDao.getPriceOnDateTimeBySymbol(symbol, LocalDateTime.now().minusDays(days));
        double valueDaysAgo = priceDaysAgo * unitsDaysAgo;
        asset.getHistoricalValues().put(("value" + xDays + "Ago"), valueDaysAgo);
        asset.getDeltaValues().put(("delta" + xDays + "Value"), asset.getCurrentValue() - valueDaysAgo);
        asset.getDeltaValues().put(("delta" + xDays + "Pct"), calculateDeltaPct(asset.getCurrentValue(), valueDaysAgo));
        return asset;
    }

    private Asset calculateValuesNrOfMonthsAgo(int accountId, Asset asset, String symbol, int months) {
        String xMonths = (months == 1) ? "1Month" : (months + "Months");
        double unitsMonthsAgo = rootRepository.getSymbolUnitsAtDateTime(accountId, symbol, LocalDateTime.now().minusMonths(months));
        asset.getHistoricalNrOfUnits().put(("units" + xMonths + "Ago"), unitsMonthsAgo);
        double priceMonthsAgo = jdbcCryptoDao.getPriceOnDateTimeBySymbol(symbol, LocalDateTime.now().minusMonths(months));
        double valueMonthsAgo = priceMonthsAgo * unitsMonthsAgo;
        asset.getHistoricalValues().put(("value" + xMonths + "Ago"), valueMonthsAgo);
        asset.getDeltaValues().put("delta" + xMonths + "Value", asset.getCurrentValue() - valueMonthsAgo);
        asset.getDeltaValues().put("delta" + xMonths + "Pct", calculateDeltaPct(asset.getCurrentValue(), valueMonthsAgo));
        return asset;
    }

    private Asset calculateValuesAtDate(int accountId, Asset asset, String symbol, LocalDateTime dateTime) {
        double unitsAtStartDate = rootRepository.getSymbolUnitsAtDateTime(accountId, symbol, dateTime);
        asset.getHistoricalNrOfUnits().put(("unitsAtStartDate"), unitsAtStartDate);
        double priceAtStartDate = jdbcCryptoDao.getPriceOnDateTimeBySymbol(symbol, dateTime);
        double valueAtStartDate = priceAtStartDate * unitsAtStartDate;
        asset.getHistoricalValues().put(("valueAtStartDate"), valueAtStartDate);
        asset.getDeltaValues().put(("deltaStartValue"), asset.getCurrentValue() - valueAtStartDate);
        asset.getDeltaValues().put(("deltaStartPct"), calculateDeltaPct(asset.getCurrentValue(), valueAtStartDate));
        return asset;
    }

    private double calculateDeltaPct(double currentValue, double historicalValue) {
        double deltaValuePct = ((currentValue - historicalValue) / historicalValue) * 100;
        if (Double.isInfinite(deltaValuePct)) {
            return 100;
        } else {
            return deltaValuePct;
        }
    }

    public double getAssetDeltaPct(int accountId, String symbol, LocalDateTime dateTime) {
        return rootRepository.getAssetDeltaPct(accountId, symbol, dateTime);
    }
}
