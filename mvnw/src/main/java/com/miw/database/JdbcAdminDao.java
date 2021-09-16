package com.miw.database;/*
package com.miw.database;

import com.miw.model.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.swing.tree.RowMapper;
import java.util.List;

// TODO A whole damn lot

public class JdbcAdminDao implements Dao<Client> {

    private final Logger logger = LoggerFactory.getLogger(JdbcAdminDao.class);
    private JdbcTemplate jdbcTemplate;

    // TODO afmaken: snap de foutmelding niet...
    RowMapper<Client> rowMapper = (rs,rowNum) -> {
            Client client = new Client();
            client.setEmail(rs.getString("email"));
            client.setPassword(rs.getString("password"));
            return client;
});

    public JdbcAdminDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Client> list() {
        String sql = "SELECT email, password from User";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public void create(Client client) {

    }

    @Override
    // an optional allows for an empty client to be returned when is doesn't exist instead of an exception
    public Optional<Client> get(int id) {
        return Optional.empty();
    }



    @Override
    public void update(Client client, int id) {

    }

    @Override
    public void delete(int id) {

    }
}
*/
