package com.miw.controller;

import com.miw.database.JdbcAccountDao;
import com.miw.database.JdbcAssetDao;
import com.miw.database.RootRepository;
import com.miw.service.StatisticsService;
import com.miw.service.authentication.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DashboardController {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private TokenService tokenService;
    private JdbcAccountDao jdbcAccountDao;
    private JdbcAssetDao jdbcAssetDao;
    private RootRepository rootRepository;
    private StatisticsService statisticsService;

    @Autowired
    public DashboardController(StatisticsService statisticsService, TokenService tokenService, JdbcAccountDao jdbcAccountDao, JdbcAssetDao jdbcAssetDao, RootRepository rootRepository) {
        super();
        this.tokenService = tokenService;
        this.jdbcAccountDao = jdbcAccountDao;
        this.jdbcAssetDao = jdbcAssetDao;
        this.rootRepository = rootRepository;
        this.statisticsService = statisticsService;
        logger.info("New DashboardController created");
    }

    //TODO: waarom komt token niet binnen???
    //TODO: dit moet een GetMapping worden. Data wordt ge-get, resource op server wordt niet aangepast.
    @GetMapping("/getBalance")
    public double getBalance(@RequestHeader("Authorization") String token) {
        int ID = TokenService.getValidUserID(token);
        try {
            //double balance = jdbcAccountDao.getAccountByUserID(ID).getBalance() +9.78204;
            double balance = Math.round((jdbcAccountDao.getAccountByUserID(ID).getBalance())*100);
            return balance/100;
        } catch (NullPointerException fault) {
            return 0.00;
        }
//        double balance = jdbcAccountDao.getAccountByUserID(ID).getBalance();
//        balance = Math.round(balance*100);
//        return balance/100;
    }


//    @PostMapping("/getPortfolioValue")
//    public double getPortfolioValue(@RequestBody String token) {
//        int ID = TokenService.getValidUserID(token);
//        List<Asset> clientAssets = jdbcAssetDao.getAssets(ID);
//        double totalValue = 0.00;
//        for (Asset asset: clientAssets) {
//            totalValue += asset.getUnits() * asset.getCurrentValue();
//        }
//        return totalValue;
//    }

    @GetMapping("/portfolio/percentageIncrease")
    public Double getPercentageIncrease(@RequestHeader("Authorization") String token){
        int ID = TokenService.getValidUserID(token);
        try {
            return statisticsService.getPercentageIncrease(ID,1); //TODO: Magic number!
        } catch (NullPointerException empty) {
            return null;
        }
    }



}


