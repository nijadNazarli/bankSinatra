/**
 * @Author: Johnny Chan
 * @Description: Controller which validates the received JWT and obtains and returns the client's portfolio
 * with current, historical and delta values.
 */
package com.miw.controller;

import com.google.gson.*;
import com.miw.database.JdbcCryptoDao;
import com.miw.database.RootRepository;
import com.miw.model.Asset;
import com.miw.service.PortfolioService;
import com.miw.service.StatisticsService;
import com.miw.service.authentication.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class PortfolioController {

    private PortfolioService portfolioService;
    private Gson gson;
    private JdbcCryptoDao jdbcCryptoDao;
    private StatisticsService statisticsService;
    private RootRepository rootRepository;
    private final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

    @Autowired
    public PortfolioController(PortfolioService portfolioService, Gson gson, RootRepository rootRepository,
                               JdbcCryptoDao jdbcCryptoDao, StatisticsService statisticsService) {
        this.jdbcCryptoDao = jdbcCryptoDao;
        this.portfolioService = portfolioService;
        this.statisticsService = statisticsService;
        this.gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                (jsonElement, type, jsonDeserializationContext) ->
                        LocalDateTime.parse(jsonElement.getAsJsonPrimitive().getAsString())).create();
        this.rootRepository = rootRepository;
    }

    @GetMapping("/portfolio")
    public ResponseEntity<?> getPortfolioOverview(@RequestHeader("Authorization") String token) {
        int userId = TokenService.getValidUserID(token);
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int accountId = portfolioService.getAccountIdByUserId(userId);
        //PortfolioService aanroepen om de vereiste gegevens te verzamelen en returnen aan frontend
        Map<String, Object> portfolio = portfolioService.getPortfolio(accountId);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/portfolio/totalPortfolioValue")
    public ResponseEntity<?> getCurrentPortfolioValue(@RequestHeader("Authorization") String token) {
        int userId = TokenService.getValidUserID(token);
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int accountId = portfolioService.getAccountIdByUserId(userId);
        double totalPortfolioValue = portfolioService.getTotalPortfolioValue(accountId);
        return ResponseEntity.ok(totalPortfolioValue);
    }

    @GetMapping("/portfolio/assets")
    public ResponseEntity<?> getAssets(@RequestHeader("Authorization") String token) {
        int userId = TokenService.getValidUserID(token);
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int accountId = portfolioService.getAccountIdByUserId(userId);
        List<Asset> assetList= portfolioService.getAssets(accountId);
        return ResponseEntity.ok(assetList);
    }

    @GetMapping("/portfolio/assetDeltaPct")
    public ResponseEntity<?> getAssetDeltaPct(@RequestHeader("Authorization") String token,
                                                @RequestHeader("Symbol") String symbol,
                                                @RequestHeader("DateTime")
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        int userId = TokenService.getValidUserID(token);
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int accountId = portfolioService.getAccountIdByUserId(userId);
        double assetDeltaPct = portfolioService.getAssetDeltaPct(accountId, symbol, dateTime);
        return ResponseEntity.ok(assetDeltaPct);
    }

    @PutMapping("/marketAsset")
    public ResponseEntity<?> marketAsset(@RequestHeader("Authorization") String token, @RequestBody String assetsSale) {
        int userId = TokenService.getValidUserID(token);
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int accountId = rootRepository.getAccountById(userId).getAccountId();
        Asset assetForSale = gson.fromJson(assetsSale, Asset.class);
        if (rootRepository.getAssetBySymbol(accountId, assetForSale.getCrypto().getSymbol()).getUnits() < assetForSale.getUnitsForSale()) {
            return new ResponseEntity<>("User does not have sufficient units", HttpStatus.BAD_REQUEST);
        }
        rootRepository.marketAsset(assetForSale.getUnitsForSale(), assetForSale.getSalePrice(), assetForSale.getCrypto().getSymbol(), accountId);
        return new ResponseEntity<>("The asset has been marketed successfully.", HttpStatus.OK);
    }

    @GetMapping("/cryptoStats")
    public ResponseEntity<?> getCryptoStats(@RequestHeader("Authorization") String token,
                                            @RequestParam("symbol") String symbol,
                                            @RequestParam("daysBack") Integer daysBack) {
        if (!TokenService.validateClient(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<>(jdbcCryptoDao.getDayValuesByCrypto(symbol, daysBack), HttpStatus.OK);
    }

    @GetMapping("/portfolioStats")
    public ResponseEntity<?> getPortfolioStats(@RequestHeader("Authorization") String token) {
        if (!TokenService.validateClient(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int userId = TokenService.getValidUserID(token);
        return new ResponseEntity<>(statisticsService.getPortfolioStats(userId, 30), HttpStatus.OK);
    }

    @PostMapping("/latestPrice")
    public ResponseEntity<?> getLatestValueBySymbol(@RequestHeader("Authorization") String token,
                                                    @RequestBody String symbol){

        if (!TokenService.validateClient(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<>(rootRepository.getLatestPriceBySymbol(symbol), HttpStatus.OK);
    }

    @PostMapping("/getUnitsForSale")
    public ResponseEntity<?> getUnitsForSale(@RequestHeader("Authorization") String token,
                                             @RequestBody String symbol) {
        int userId = TokenService.getValidUserID(token);
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int accountId = portfolioService.getAccountIdByUserId(userId);

        Map<Double, Double> unitsForSaleWithPrice;
        unitsForSaleWithPrice = rootRepository.getUnitsForSaleWithPrice(symbol, accountId);
        return new ResponseEntity<>(unitsForSaleWithPrice, HttpStatus.OK);
    }




}
