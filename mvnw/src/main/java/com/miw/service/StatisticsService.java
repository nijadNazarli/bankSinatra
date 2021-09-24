package com.miw.service;

import com.miw.database.JdbcTransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class StatisticsService {

    private final PortfolioService portfolioService;
    private final JdbcTransactionDao jdbcTransactionDao;


    @Autowired
    public StatisticsService(JdbcTransactionDao jdbcTransactionDao, PortfolioService portfolioService) {
        this.jdbcTransactionDao = jdbcTransactionDao;
        this.portfolioService = portfolioService;

    }


    public Map<LocalDate, Double> getPortfolioStats(int userID, int daysBack) {
        Map<LocalDate, Double> portfolioValues = new TreeMap<>();

        //iterate through days to get cumulative values
        for (int days = daysBack; days >= 0; days--) {
            double portfolioValue = jdbcTransactionDao.getPortfolioValueByDate(userID, days);
            LocalDate date = LocalDate.now().minusDays(days);
            portfolioValues.put(date,portfolioValue);
        }
        return portfolioValues;
    }

    public double getPercentageIncrease(int userID, int daysBack){
        double pastVal = jdbcTransactionDao.getPortfolioValueByDate(userID, daysBack);
        double currentVal = portfolioService.getTotalPortfolioValue(userID);
        return ((currentVal/pastVal)*100)-100;
    }

    //TODO: portfolio waarde tenopzichte van vorige dag/week/maand
}
