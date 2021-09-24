package com.miw.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JdbcTransactionDaoTest {

    @Autowired
    private JdbcTransactionDao jdbcTransactionDao;


    @Test
    void getBankCosts() {
        double expected = 0.01;
        double actual = jdbcTransactionDao.getBankCosts();
        assertEquals(expected, actual);
    }

}