package com.miw.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JdbcCryptoDaoTest {

    @Autowired
    private JdbcCryptoDao jdbcCryptoDao;

    @Test
    void getCryptoBySymbol() {
    }

    @Test
    void getLatestPriceBySymbol() {
    }

    @Test
    void getPastPriceBySymbol() {
    }

    @Test
    void getPriceOnDateTimeBySymbol() {
    }

    @Test
    void getDayValuesByCrypto() {
        Map<LocalDate, Map<String, Double>> cryptoStats = new TreeMap<>();
        cryptoStats = jdbcCryptoDao.getDayValuesByCrypto("BTC", 100);
        for (Map.Entry<LocalDate, Map<String, Double>> entry : cryptoStats.entrySet()) {
            System.out.println(entry.getKey() + " avg: " + entry.getValue().get("avg")
                    + " min: " + entry.getValue().get("min")
                    + " max: " + entry.getValue().get("max"));
        }
    }

    @Test
    void saveCryptoPriceBySymbol() {
    }

    @Test
    void getAllCryptos() {
    }
}