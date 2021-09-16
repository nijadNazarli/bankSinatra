package miw.database;

import com.miw.model.Crypto;
import com.miw.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

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
        String sql = "SELECT * FROM `Bankingfee`";
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

    // TODO: nog testen of dit werkt
    public List<Transaction> getTransactionsByUserId (int userId) {
        String sql = "SELECT * FROM Transaction WHERE accountID_buyer = (SELECT accountID FROM Account WHERE userID = ?) " +
                "OR accountID_seller = (SELECT accountID FROM Account WHERE userID = ?)";
        return jdbcTemplate.query(sql, new TransactionRowMapper(), userId, userId);
    }

//    public Double getCryptoUnitsByDate(String crytpoSymbol, Date date) {
//        String sql = "SELECT "
//    }

    private static class TransactionRowMapper implements RowMapper<Transaction> {

        @Override
        public Transaction mapRow(ResultSet resultSet, int i) throws SQLException {
            int transactionId = resultSet.getInt("transactionID");
            int buyer = resultSet.getInt("accountID_buyer");
            int seller = resultSet.getInt("accountID_seller");
            Crypto crypto = new Crypto();
            double units = resultSet.getDouble("units");
            double transactionPrice = resultSet.getDouble("transactionPrice");
            double bankCosts = resultSet.getDouble("bankingFee");
            LocalDateTime transactionDate = resultSet.getObject("date", LocalDateTime.class);
            Transaction transaction = new Transaction(transactionId, units, buyer, seller, crypto, transactionPrice,
                    bankCosts, transactionDate);
            return transaction;
        }
    }
}