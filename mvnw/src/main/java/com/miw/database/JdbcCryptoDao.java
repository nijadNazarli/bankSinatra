package miw.database;

import com.miw.model.Crypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// TODO: id's uit crypto wegwerken, primary key -> symbol

@Repository
public class JdbcCryptoDao {

    private final Logger logger = LoggerFactory.getLogger(JdbcCryptoDao.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcCryptoDao(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JdbcCryptoDao");
    }

    // insert met gebruik van symbol ipv id
    private PreparedStatement insertPriceStatement
    (String symbol, double price, LocalDateTime time, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) " +
                        "VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        ps.setString(1, symbol);
        ps.setDouble(2, price);
        ps.setTimestamp(3, Timestamp.valueOf(time));
        return ps;
    }

    // Selecteert een crypto uit de db, neemt daarbij automatisch de meest recente opgeslagen prijs in het object op
    public Crypto getCryptoBySymbol(String symbol) {
        String sql = "SELECT Crypto.*, CryptoPrice.cryptoPrice " +
                "FROM Crypto LEFT JOIN CryptoPrice " +
                "ON Crypto.symbol = CryptoPrice.symbol " +
                "WHERE Crypto.symbol = ? " +
                "AND CryptoPrice.dateRetrieved = " +
                "    (SELECT MAX(dateRetrieved) " +
                "    FROM CryptoPrice);";
        try {
            return jdbcTemplate.queryForObject(sql, new CryptoRowMapper(), symbol);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Failed to get crypto by symbol");
            return null;
        }
    }

    // Haalt enkel de laatste koers van een gegeven crypto op
    public double getLatestPriceBySymbol(String symbol) {
        return getCryptoBySymbol(symbol).getCryptoPrice();
    }

    // Vraagt de koers op die het dichtst bij het tijdstip [nu minus X aantal uur in het verleden] is opgeslagen
    public double getPastPriceBySymbol(String symbol, int hoursAgo) {
        String sql = "SELECT cryptoPrice FROM CryptoPrice WHERE symbol = ? " +
                "ORDER BY ABS(TIMESTAMPDIFF(second, dateRetrieved, (NOW() - INTERVAL ? HOUR))) LIMIT 1;";
        try {
            return jdbcTemplate.queryForObject(sql, Double.class, symbol, hoursAgo);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Failed to get past crypto price by symbol");
            return 0;
        }
    }

    public double getPriceOnDateTimeBySymbol(String symbol, LocalDateTime dateTime) {
        String sql = "SELECT cryptoPrice FROM CryptoPrice WHERE symbol = ? " +
                "ORDER BY ABS(TIMESTAMPDIFF(second, dateRetrieved, ?)) LIMIT 1;";
        try {
            return jdbcTemplate.queryForObject(sql, Double.class, symbol, dateTime);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Failed to get selected crypto price by symbol on the selected dateTime.");
            return 0;
        }
    }

    // Returns map of crypto values with accommodating key ("min", "max", "avg") of each day from now until certain past date
    public Map<LocalDate, Map<String, Double>> getDayValuesByCrypto(String symbol, int daysBack) {
        String sql = "Select symbol, AVG(cryptoPrice) avg, MIN(cryptoPrice) min, MAX(cryptoPrice) max, date(dateRetrieved) FROM cryptoprice WHERE symbol = ? " +
                "AND dateRetrieved BETWEEN date_add(current_timestamp(),  INTERVAL -? DAY) AND current_timestamp() GROUP BY DATE(dateRetrieved);";
        try {
            return jdbcTemplate.query(sql, new CryptoStatsSetExtractor(), symbol, daysBack);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Failed to get average, minimum and max crypto values per day of the following crypto: " + symbol);
            return null;
        }
    }

    public void saveCryptoPriceBySymbol(String symbol, double price, LocalDateTime time) {
        jdbcTemplate.update(connection -> insertPriceStatement(symbol, price, time, connection));
    }

    public List<Crypto> getAllCryptos() {
        String sql = "SELECT c.symbol, description, name, cryptoPrice, dateRetrieved " +
                "FROM Crypto c JOIN CryptoPrice p ON c.symbol = p.symbol " +
                "WHERE dateRetrieved >= DATE_ADD( " +
                "   (SELECT dateRetrieved FROM CryptoPrice " +
                "   ORDER BY ABS(TIMESTAMPDIFF(second, dateRetrieved, CURRENT_TIMESTAMP)) LIMIT 1) " +
                "   , INTERVAL -10 SECOND) " +
                "   AND " +
                "   dateRetrieved <= DATE_ADD( " +
                "   (SELECT dateRetrieved FROM CryptoPrice " +
                "   ORDER BY ABS(TIMESTAMPDIFF(second, dateRetrieved, CURRENT_TIMESTAMP)) LIMIT 1) " +
                "   , INTERVAL 10 SECOND);";
        try {
            return jdbcTemplate.query(sql, new CryptoRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.info("Failed to get past crypto price by symbol");
            return null;
        }
    }

    //Rowmappers
    private static class CryptoStatsSetExtractor implements ResultSetExtractor<Map<LocalDate, Map<String, Double>>> {

        @Override
        public Map<LocalDate, Map<String, Double>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Map<LocalDate, Map<String, Double>> cryptoStatsTotal = new TreeMap<>();
            while (resultSet.next()) {
                double avg = resultSet.getDouble("avg");
                double min = resultSet.getDouble("min");
                double max = resultSet.getDouble("max");
                LocalDate date = resultSet.getDate("date(dateRetrieved)").toLocalDate();
                Map<String, Double> cryptoStats = new TreeMap<>();
                cryptoStats.put("avg", avg);
                cryptoStats.put("min", min);
                cryptoStats.put("max", max);
                cryptoStatsTotal.put(date, cryptoStats);
            }
            return cryptoStatsTotal;
        }
    }



    private static class CryptoRowMapper implements RowMapper<Crypto> {

        @Override
        public Crypto mapRow(ResultSet resultSet, int i) throws SQLException {
            String name = resultSet.getString("name");
            String symbol = resultSet.getString("symbol");
            String description = resultSet.getString("description");
            Double price = resultSet.getDouble("cryptoPrice");
            Crypto crypto = new Crypto(name, symbol, description, price);
            return crypto;
        }
    }
}
