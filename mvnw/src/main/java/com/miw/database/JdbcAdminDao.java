package miw.database;

import com.miw.model.Administrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class JdbcAdminDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger(JdbcAdminDao.class);

    private PreparedStatement insertAdminStatement(Administrator admin, Connection connection) {
        String sql = "INSERT INTO User (email, password, salt, userRole, isBlocked, firstName, prefix, lastName) " +
                "VALUES (?, ?, ?, 'admin', 1, ?, ?, ?);";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, admin.getEmail());
            ps.setString(2, admin.getPassword());
            ps.setString(3, admin.getSalt());
            ps.setString(4, admin.getFirstName());
            ps.setString(5, admin.getPrefix());
            ps.setString(6, admin.getLastName());
            return ps;
        } catch (SQLException sqlException) {
            System.out.println("Sql exception at JdbcAdminDAO" + sqlException);
        }
        return ps;
    }

    public Administrator save(Administrator admin) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> insertAdminStatement(admin, connection), keyHolder);
        int userId = keyHolder.getKey().intValue();
        admin.setUserId(userId);
        return admin;
    }

    public Administrator findByEmail(String email) {
        String sql = "SELECT * FROM User WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new AdminRowMapper(), email);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Admin does not exist in the database");
            return null;
        }
    }

    private static class AdminRowMapper implements RowMapper<Administrator> {

        @Override
        public Administrator mapRow(ResultSet resultSet, int i) throws SQLException {
            int id = resultSet.getInt("userID");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            String salt = resultSet.getString("salt");
            String firstName = resultSet.getString("firstName");
            String prefix = resultSet.getString("prefix");
            String lastName = resultSet.getString("lastName");
            boolean isBlocked = resultSet.getBoolean("isBlocked");
            Administrator admin = new Administrator(email, password, salt, firstName, prefix, lastName);
            admin.setBlocked(isBlocked);
            admin.setUserId(id);
            return admin;
        }
    }
}