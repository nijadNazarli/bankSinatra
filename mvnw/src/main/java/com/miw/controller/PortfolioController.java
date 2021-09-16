/**
 * @Author: Johnny Chan
 * @Description: Controller which validates the received JWT and obtains and returns the client's portfolio
 * with current, historical and delta values.
 */
package miw.controller;

import com.google.gson.Gson;
import com.miw.database.JdbcCryptoDao;
import com.miw.database.RootRepository;
import com.miw.model.Asset;
import com.miw.service.PortfolioService;
import com.miw.service.authentication.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PortfolioController {

    private PortfolioService portfolioService;
    private Gson gson;
    private JdbcCryptoDao jdbcCryptoDao;
    private RootRepository rootRepository;
    private final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

    @Autowired
    public PortfolioController(PortfolioService portfolioService, Gson gson, RootRepository rootRepository,
                               JdbcCryptoDao jdbcCryptoDao) {
        this.jdbcCryptoDao = jdbcCryptoDao;
        this.portfolioService = portfolioService;
        this.gson = gson;
        this.rootRepository = rootRepository;
    }

    @GetMapping("/portfolio")
    public ResponseEntity<?> getPortfolioOverview(@RequestHeader("Authorization") String token) {
        Integer userId = TokenService.getValidUserID(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //PortfolioService aanroepen om de vereiste gegevens te verzamelen en returnen aan frontend
        Map<String, Object> portfolio = portfolioService.getPortfolio(userId);
        return ResponseEntity.ok(portfolio);
    }

    @PutMapping("/marketAsset")
    public ResponseEntity<?> marketAsset(@RequestHeader("Authorization") String token, @RequestBody String assetsSale) {
        Integer userId = TokenService.getValidUserID(token);
        if (userId == null) {
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




}
