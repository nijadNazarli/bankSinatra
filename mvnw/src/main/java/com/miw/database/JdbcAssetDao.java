/**
 * @Author Johnny Chan
 * Deze DAO-class haalt de crypto-assets uit de SQL-database.
 */
package com.miw.database;

import com.miw.model.Account;
import com.miw.model.Asset;
import com.miw.model.Crypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Repository
public class JdbcAssetDao {

    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(JdbcAssetDao.class);

    public JdbcAssetDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("JdbcAssetDAO-object created.");
    }

    public void saveAsset(int accountID, String symbol, double units) {
        jdbcTemplate.update(connection -> insertAssetStatement(accountID, symbol, units, connection));
        logger.info("New asset has been saved to the database.");
    }

    private PreparedStatement insertAssetStatement (int accountId, String symbol, double units, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO Asset (accountID, symbol, units)" + "VALUES (?, ?, ?)");
        ps.setInt(1, accountId);
        ps.setString(2, symbol);
        ps.setDouble(3, units);
        return ps;
    }

    public Asset getAssetBySymbol(int accountId, String symbol) throws EmptyResultDataAccessException {
        String sql = "SELECT a.accountId, a.unitsForSale, a.salePrice, a.symbol, name, cryptoPrice, units, description, dateRetrieved" +
                " FROM (Asset a JOIN Crypto c ON a.symbol = c.symbol) JOIN CryptoPrice p ON p.symbol = c.symbol " +
                "WHERE accountID = ? AND a.symbol = ? AND dateRetrieved >= DATE_ADD(" +
                "(SELECT dateRetrieved FROM CryptoPrice ORDER BY ABS(TIMESTAMPDIFF(second, dateRetrieved, CURRENT_TIMESTAMP))" +
                " LIMIT 1), INTERVAL -10 SECOND) AND dateRetrieved <= DATE_ADD(" +
                " (SELECT dateRetrieved FROM CryptoPrice ORDER BY ABS(TIMESTAMPDIFF(second, dateRetrieved, CURRENT_TIMESTAMP))" +
                " LIMIT 1), INTERVAL 10 SECOND);";
        try {
            return jdbcTemplate.queryForObject(sql, new AssetRowMapper(), accountId, symbol);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<Asset> getAllAssetsForSaleBySymbol(String symbol, int accountId){
        String sql = "SELECT accountID, a.symbol, name, cryptoPrice, description, units, unitsForSale, salePrice" +
                " FROM (Asset a JOIN Crypto c ON a.symbol = c.symbol) " +
                "JOIN CryptoPrice p ON p.symbol = c.symbol " +
                "WHERE a.symbol = ? AND unitsForSale > 0 AND ACCOUNTID != ? " +
                "AND dateRetrieved >= DATE_ADD( (SELECT dateRetrieved FROM CryptoPrice " +
                "ORDER BY ABS(TIMESTAMPDIFF(second, dateRetrieved, CURRENT_TIMESTAMP)) LIMIT 1), " +
                "INTERVAL -0.00001 SECOND) AND dateRetrieved <= DATE_ADD( " +
                "(SELECT dateRetrieved FROM CryptoPrice " +
                "ORDER BY ABS(TIMESTAMPDIFF(second, dateRetrieved, CURRENT_TIMESTAMP)) LIMIT 1), INTERVAL 0.00001 SECOND);";
        try {
            return jdbcTemplate.query(sql, new AssetRowMapper(), symbol, accountId);
        } catch (EmptyResultDataAccessException e){
            logger.info("No data available");
            return null;
        }
    }

    public List<Asset> getAssets(int accountId) {
        String sql = "SELECT a.unitsForSale, a.salePrice, a.accountID, a.symbol, name, cryptoPrice, units, description, dateRetrieved " +
                "FROM (Asset a JOIN Crypto c ON a.symbol = c.symbol) " +
                "JOIN CryptoPrice p ON p.symbol = c.symbol " +
                "WHERE accountID = ? AND dateRetrieved >= DATE_ADD(" +
                "   (SELECT dateRetrieved FROM CryptoPrice " +
                "   ORDER BY ABS(TIMESTAMPDIFF(second, dateRetrieved, CURRENT_TIMESTAMP)) LIMIT 1)" +
                "   , INTERVAL -10 SECOND)" +
                "AND dateRetrieved <= DATE_ADD(" +
                "   (SELECT dateRetrieved FROM CryptoPrice " +
                "   ORDER BY ABS(TIMESTAMPDIFF(second, dateRetrieved, CURRENT_TIMESTAMP)) LIMIT 1)" +
                "   , INTERVAL 10 SECOND);";
        try {
            return jdbcTemplate.query(sql, new AssetRowMapper(), accountId);
        } catch (EmptyResultDataAccessException e){
            logger.info("No data available");
            return null;
        }
    }

    public Double getSymbolUnitsAtDateTime(int accountId, String symbol, LocalDateTime dateTime) {
        String sql = "SELECT (" +
                "   (SELECT units " +
                "   FROM Asset " +
                "   WHERE accountID = ? AND symbol = ?) " +
                "   +" +
                "   (SELECT IFNULL(SUM(units), 0) FROM `Transaction` " +
                "   WHERE accountID_seller = ? AND symbol = ? AND `date` BETWEEN ? AND CURRENT_TIMESTAMP()) " +
                "   - " +
                "   (SELECT IFNULL(SUM(units), 0) FROM `Transaction` " +
                "   WHERE accountID_buyer = ? AND symbol = ? AND `date` BETWEEN ? AND CURRENT_TIMESTAMP())) " +
                "AS unitsAtDateTime;";
        Double units = jdbcTemplate.queryForObject(sql, Double.class, accountId, symbol, accountId, symbol, dateTime,
                accountId, symbol, dateTime);
        return (units != null) ? units : 0.0;
    }

    public void updateAsset(double newUnits, String symbol, int accountId){
        String updateQuery = "UPDATE Asset SET units = ? WHERE symbol = ? AND accountID = ?;";
        try {
            jdbcTemplate.update(updateQuery, newUnits, symbol, accountId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User does not have such assets");
        }
    }

    public void updateAssetForSale(double newUnits, String symbol, int accountId){
        String updateQuery = "UPDATE Asset set unitsForSale = ? WHERE symbol = ? AND accountID = ?;";
        try {
            jdbcTemplate.update(updateQuery, newUnits, symbol, accountId);
        } catch (Exception e){
            e.printStackTrace();
            logger.warn("Unable to update assets for sale");
        }
    }

    public void deleteAsset(String symbol, int accountId){
        String deleteQuery = "DELETE FROM Asset WHERE symbol = ? AND accountID =?";
        jdbcTemplate.update(deleteQuery, symbol, accountId);
    }

    public void putAssetOnSale(double units, double salePrice, String symbol, int accountId) {
        String putAssetOnSale = "UPDATE Asset SET unitsForSale = ?, salePrice = ? WHERE symbol = ? AND accountID = ?;";
        try {
            jdbcTemplate.update(putAssetOnSale, units, salePrice, symbol, accountId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("User does not have enough units in the portfolio");
        }
    }

    public Map<Double, Double> getUnitsForSaleAndPrice(String symbol, int accountId) {
        String sql = "SELECT unitsForSale, salePrice FROM Asset WHERE symbol = ? AND accountID = ?;";
        try {
            Map<Double, Double> unitsForSaleWithPrice = new HashMap<>();
            unitsForSaleWithPrice = jdbcTemplate.query(sql, new UnitsForSaleExtractor(), symbol, accountId);
            return unitsForSaleWithPrice;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public double getAssetDeltaPct(int accountId, String symbol, LocalDateTime dateTime) {
        String sql = "SELECT ROUND(((q1.currentValue - q2.pastValue)/q2.pastValue * 100), 2) AS deltaPct " +
                "FROM " +
                "(SELECT c.symbol, (a.units * c.cryptoPrice) currentValue " +
                "FROM CryptoPrice c JOIN Asset a ON c.symbol = a.symbol " +
                "WHERE c.symbol = ? AND accountID = ? " +
                "ORDER BY ABS(TIMESTAMPDIFF(second, dateRetrieved, CURRENT_TIMESTAMP)) LIMIT 1) AS q1 " +
                "JOIN " +
                "(SELECT c.symbol, (a.units * c.cryptoPrice) pastValue " +
                "FROM CryptoPrice c JOIN Asset a ON c.symbol = a.symbol " +
                "WHERE c.symbol = ? AND accountID = ? " +
                "ORDER BY ABS(TIMESTAMPDIFF(second, dateRetrieved, ?)) LIMIT 1) AS q2 " +
                "ON q1.symbol = q2.symbol;";
        return jdbcTemplate.queryForObject(sql, Double.class, symbol, accountId, symbol, accountId, dateTime);
    }

    private static class AssetRowMapper implements RowMapper<Asset> {

        @Override
        public Asset mapRow(ResultSet resultSet, int i) throws SQLException {
            int accountId = resultSet.getInt("accountID");
            String name = resultSet.getString("name");
            String symbol = resultSet.getString("symbol");
            String description = resultSet.getString("description");
            double cryptoPrice = resultSet.getDouble("cryptoPrice");
            Crypto crypto = new Crypto(name, symbol, description, cryptoPrice);
            double units = resultSet.getDouble("units");
            double unitsForSale = resultSet.getDouble("unitsForSale");
            double salePrice = resultSet.getDouble("salePrice");
            Asset asset = new Asset(crypto, units, unitsForSale, salePrice);
            asset.setAccountId(accountId);
            return asset;
        }
    }


    // ALTERNATIEF ALS DE WAARDEBEREKENINGEN BIJ AANMAAK VAN ASSETS IN ASSET CLASS ZELF GEBEUREN EN DE GEMAAKTE ASSETS DIRECT IN EEN MAP KUNNEN

    public Map<String, Asset> getAssetsMap(int accountId) {
        String sql = "SELECT accountID, a.symbol, `name`, symbol, cryptoPrice, `description`, units, `dateRetrieved` " +
                "FROM (Asset a JOIN Crypto c ON a.symbol = c.symbol) " +
                "JOIN CryptoPrice p ON p.symbol = c.symbol " +
                "WHERE accountID = ?;";
        return jdbcTemplate.query(sql, new AssetResultSetExtractor(), accountId);
    }


    private static class AssetResultSetExtractor implements ResultSetExtractor<Map<String, Asset>> {

        @Override
        public Map<String, Asset> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Map<String, Asset> assetMap = new TreeMap<>();
            while(resultSet.next()) {
                String name = resultSet.getString("name");
                String symbol = resultSet.getString("symbol");
                String description = resultSet.getString("description");
                double cryptoPrice = resultSet.getDouble("cryptoPrice");
                Crypto crypto = new Crypto(name, symbol, description, cryptoPrice);
                double units = resultSet.getDouble("units");
                Asset asset = new Asset(crypto, units);
                assetMap.put(symbol, asset);
            }
            return assetMap;
        }
    }

    private static class UnitsForSaleExtractor implements ResultSetExtractor<Map<Double, Double>>{
        @Override
        public Map<Double, Double> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Map<Double, Double> unitsForSaleWithPrice = new HashMap<>();
            while (resultSet.next()) {
                double unitsForSale = resultSet.getDouble("unitsForSale");
                double salePrice = resultSet.getDouble("salePrice");
                unitsForSaleWithPrice.put(unitsForSale, salePrice);
            }
            return unitsForSaleWithPrice;
        }
    }

}
