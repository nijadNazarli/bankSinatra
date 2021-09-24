package com.miw.database;

import com.miw.model.Crypto;
import com.miw.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Repository
public class JdbcTransactionDao {

    private final Logger logger = LoggerFactory.getLogger(JdbcTransactionDao.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTransactionDao(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JdbcTransactionDao");
    }

    private PreparedStatement insertTransactionStatement(Transaction transaction, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO Transaction " +
                "(date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        ps.setObject(1, transaction.getTransactionDate());
        ps.setDouble(2, transaction.getUnits());
        ps.setDouble(3, transaction.getTransactionPrice());
        ps.setDouble(4, transaction.getBankCosts());
        ps.setInt(5, transaction.getBuyer());
        ps.setInt(6, transaction.getSeller());
        ps.setString(7, transaction.getCrypto().getSymbol());
        return ps;
    }

    public Transaction save(Transaction transaction) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> insertTransactionStatement(transaction, connection), keyHolder);
        int transactionId = keyHolder.getKey().intValue();
        transaction.setTransactionId(transactionId);
        logger.info("New transaction has been saved to the database");
        return transaction;
    }

    public double getBankCosts() {
        String sql = "SELECT * FROM `BankingFee`";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    public void updateBankCosts(double fee) {
        String sql = "UPDATE BankingFee SET percentage = ?";
        jdbcTemplate.update(sql, fee);
    }

    public double getSumOfUnitsPurchasedAndSold(int accountId, LocalDateTime dateTime, String symbol) {
        String sql = "SELECT " +
                "(SELECT IFNULL(SUM(units), 0) FROM `Transaction` " +
                "WHERE accountID_buyer = ? AND date BETWEEN ? AND current_timestamp() AND symbol = ?) " +
                "-" +
                "(SELECT IFNULL(SUM(units), 0) FROM `Transaction` " +
                "WHERE accountID_seller = ? AND date BETWEEN ? AND current_timestamp() AND symbol = ?)" +
                "AS sumOfUnitsPurchasedAndSold;";
        try {
            return jdbcTemplate.queryForObject(sql, Double.class, accountId, dateTime, symbol, accountId, dateTime, symbol);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public List<String> getAllCryptosOwned(int accountId) {
        String sql = "(SELECT symbol FROM `Transaction` WHERE accountID_buyer = ? OR accountID_seller = ? " +
                "GROUP BY symbol) UNION (SELECT symbol FROM Asset WHERE accountID = ?);";
        return jdbcTemplate.query(sql, (resultSet, i) -> resultSet.getString("symbol"),
                accountId, accountId, accountId);
    }

    public LocalDateTime getDateTimeOfFirstTransaction(int accountId) {
        String sql = "SELECT MIN(date) FROM `Transaction` WHERE accountID_buyer = ? OR accountID_seller = ?;";
        LocalDateTime datetime = jdbcTemplate.queryForObject(sql, LocalDateTime.class, accountId, accountId);
        return datetime;
    }

    public List<Transaction> getTransactionsByUserIdSeller (int userId) {
        String sql = "SELECT * FROM Transaction t JOIN Crypto c ON t.symbol = c.symbol " +
                "WHERE accountID_seller = (SELECT accountID from Account WHERE userID = ?) " +
                "ORDER BY date desc;";
        return jdbcTemplate.query(sql, new TransactionRowMapper(), userId);
    }

    public List<Transaction> getTransactionsByUserIdBuyer(int userId){
        String sql = "SELECT * FROM Transaction t JOIN Crypto c ON t.symbol = c.symbol " +
                "WHERE accountID_buyer = (SELECT accountID from Account WHERE userID = ?) " +
                "ORDER BY date desc; ";
        return jdbcTemplate.query(sql, new TransactionRowMapper(), userId);
    }

    public Map<LocalDate, Map<String, Double>> getBoughtUnitsPerDay(int userID) {
        String sql = "SELECT date(date) date, symbol, units FROM Transaction WHERE accountID_buyer = ? GROUP BY date, symbol";;
        try {
            return jdbcTemplate.query(sql, new UnitsSetExtractor(), userID);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Failed to retrieve bought units of each crypto per day");
            return null;
        }
    }

    public Map<LocalDate, Map<String, Double>> getSoldUnitsPerDay(int userID) {
        String sql = "SELECT date(date) date, symbol, units FROM Transaction WHERE accountID_seller = ? GROUP BY date, symbol";;
        try {
            return jdbcTemplate.query(sql, new UnitsSetExtractor(), userID);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Failed to retrieve sold units of each crypto per day");
            return null;
        }
    }

    // Returns total portfolio value of 1 day,
    // daysBack =
    // vandaag = 0, gisteren = 1, eergisteren = 2 etc.
    public double getPortfolioValueByDate(int userID, int daysBack) {
        String sql = "SELECT coalesce(SUM(ROUND((cryptoPrice * tr.totalUnits), 2)),0) portfolioValue " +
                "FROM " +
                "(SELECT symbol, date(dateRetrieved), cryptoPrice " +
                "FROM CryptoPrice WHERE dateRetrieved = " +
                "(SELECT max(dateRetrieved) " +
                "FROM CryptoPrice WHERE dateRetrieved " +
                "BETWEEN timestamp(curdate() -?) AND timestamp(curdate() +(1-?)))) cr " +
                "JOIN " +
                "(SELECT bought.symbol, ifnull(bought.units-sold.units, bought.units) totalUnits " +
                "FROM (SELECT SUM(units) units, accountID_buyer, symbol " +
                "FROM Transaction " +
                "WHERE accountID_buyer = ? " +
                "AND date < timestamp(curdate()+(1-?)) GROUP BY symbol) bought " +
                "LEFT JOIN \n" +
                "(SELECT SUM(units) units, accountID_seller, symbol FROM Transaction " +
                "WHERE accountID_seller = ? " +
                "AND date < timestamp(curdate()+(1-?)) GROUP BY symbol) sold " +
                "ON bought.symbol = sold.symbol) tr " +
                "ON cr.symbol = tr.symbol;";
        try {
            return jdbcTemplate.queryForObject(sql, Double.class, daysBack, daysBack, userID, daysBack, userID, daysBack);
        } catch (NullPointerException e) {
            return 0.0;
        }
    }



    // Rowmappers

    private static class UnitsSetExtractor implements ResultSetExtractor<Map<LocalDate, Map<String, Double>>> {

        @Override
        public Map<LocalDate, Map<String, Double>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Map<LocalDate, Map<String, Double>> unitsPerDate = new TreeMap<>();
            while (resultSet.next()) {
                String symbol = resultSet.getString("symbol");
                double units = resultSet.getDouble("units");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                if (unitsPerDate.containsKey(date)){
                    unitsPerDate.get(date).put(symbol,units);
                }
                else{
                    Map<String, Double> unitsPerSymbol = new TreeMap<>();
                    unitsPerSymbol.put(symbol, units);
                    unitsPerDate.put(date, unitsPerSymbol);
                }
            }
            return unitsPerDate;
        }
    }
    

    private static class TransactionRowMapper implements RowMapper<Transaction> {

        @Override
        public Transaction mapRow(ResultSet resultSet, int i) throws SQLException {
            int transactionId = resultSet.getInt("transactionID");
            int buyer = resultSet.getInt("accountID_buyer");
            int seller = resultSet.getInt("accountID_seller");
            double units = resultSet.getDouble("units");
            String symbol = resultSet.getString("symbol");
            Crypto crypto = new Crypto();
            crypto.setSymbol(symbol);
            double transactionPrice = resultSet.getDouble("transactionPrice");
            double bankCosts = resultSet.getDouble("bankingFee");
            LocalDateTime transactionDate = resultSet.getObject("date", LocalDateTime.class);
            Transaction transaction = new Transaction(transactionId, units, buyer, seller, crypto, transactionPrice,
                    bankCosts, transactionDate);
            return transaction;
        }
    }
}