package com.miw.database;

import com.miw.model.Administrator;
import com.miw.model.Client;
import com.miw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class JdbcUserDao {
    private JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(JdbcUserDao.class);

    @Autowired
    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }

    //public String getClientAdminByID()

    public String getRoleByEmail(String email) {
        String sql = "SELECT userRole FROM User WHERE email = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, email);
        } catch (EmptyResultDataAccessException e) {
            logger.info("User does not exist in the database");
            return null;
        }
    }

    public String getRoleByID(int userID) {
        String sql = "SELECT userRole FROM User WHERE userID= ?;";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, userID);
        } catch (EmptyResultDataAccessException e) {
            logger.info("User does not exist in the database");
            return null;
        }
    }

    public String getFirstNameById(int userId){
        String sql = "SELECT firstName FROM User WHERE userID = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, userId);
        } catch (EmptyResultDataAccessException e) {
            logger.info("User does not exist in the databse");
            return null;
        }
    }

    public int getIDByEmail(String email) {
        String sql = "SELECT userID FROM User WHERE email= ?;";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, email);
        } catch (EmptyResultDataAccessException e) {
            logger.info("User does not exist in the database");
            return 0;
        }
    }

    public User getUserByEmail(String email){
        String sql = "SELECT * FROM User WHERE email = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(), email);
        } catch (EmptyResultDataAccessException exception) {
            logger.info("The user with such email does not exist in the database");
            return null;
        }
    }

    public Boolean checkIfBlockedByID(int userID){
        String sql = "SELECT isBlocked FROM User WHERE userID = ?;";
        try {
            return (jdbcTemplate.queryForObject(sql, Boolean.class, userID =1));
        } catch (EmptyResultDataAccessException exception) {
            logger.info("The user with ID" + userID + " does not exist in the database");
            return true;
        }
    }

    public void toggleBlock(boolean blockUnblock, int id) {
        String sql = "UPDATE User SET isBlocked = ? WHERE userID = ?;";
        jdbcTemplate.update(sql, blockUnblock, id);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            int id = resultSet.getInt("userID");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            String salt = resultSet.getString("salt");
            String role = resultSet.getString("userRole");
            boolean isBlocked = resultSet.getBoolean("isBlocked");
            boolean isAdmin = role.equals("admin");

            if (isAdmin) {
                Administrator admin = new Administrator(email, password);
                admin.setUserId(id);
                admin.setBlocked(isBlocked);
                admin.setSalt(salt);
                return admin;
            } else {
                Client client = new Client(email, password);
                client.setUserId(id);
                client.setBlocked(isBlocked);
                client.setSalt(salt);
                return client;
            }
        }
    }

}





