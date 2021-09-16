package com.miw.database;

import com.miw.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class JdbcAccountDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private PreparedStatement insertAccountStatement(Account account, int userId, Connection connection) throws SQLException {
        String sql = "INSERT INTO Account (IBAN, balance, userID) VALUES(?, ?, ?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, account.getIban());
        preparedStatement.setDouble(2, account.getBalance());
        preparedStatement.setInt(3, userId);
        return preparedStatement;
    }

    public Account save(Account account, int userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> insertAccountStatement(account, userId, connection), keyHolder);
        int accountId = keyHolder.getKey().intValue();
        account.setAccountId(accountId);
        return account;
    }

}
