package com.miw.controller;

import com.google.gson.Gson;
import com.miw.database.RootRepository;
import com.miw.model.*;
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
public class MarketplaceController {

    private RootRepository rootRepository;
    private final Logger logger = LoggerFactory.getLogger(MarketplaceController.class);
    private int accountBank;

    @Autowired
    public MarketplaceController(RootRepository rootRepository){
        this.rootRepository = rootRepository;
        accountBank = Bank.BANK_ID;
        logger.info("New MarketplaceController-object created");
    }

    @PostMapping("/requestCryptos")
    public ResponseEntity<?> getAssetsForSale(@RequestHeader("Authorization") String token, @RequestBody String symbol){
        if (!TokenService.validateJWT(token)) {
            return new ResponseEntity<>("Invalid login credentials, try again", HttpStatus.UNAUTHORIZED);
        }
        int userId = TokenService.getValidUserID(token);
        if (userId == 0){
            return new ResponseEntity<>("booh", HttpStatus.UNAUTHORIZED);
        }

        int accountId = rootRepository.getAccountByUserId(TokenService.getValidUserID(token)).getAccountId();

        List<Asset> assetsForSale = rootRepository.getAllAssetsForSaleBySymbol(symbol, accountId);

        if (assetsForSale != null){
            return new ResponseEntity<>(assetsForSale, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No assets available for sale", HttpStatus.BAD_REQUEST); //TODO: checken of dit de juiste statuscode is
        }
    }

    @PostMapping("/requestName")
    public ResponseEntity<?> getNameByAccountID(@RequestBody String accountIdAsJson){
        int accountId = new Gson().fromJson(accountIdAsJson, Integer.class);

        String name;
        if (accountId == accountBank){
            name = "Bank Sinatra";
        } else {
            Client client = rootRepository.findByAccountId(accountId);
            if (client != null) {
                name = client.getFirstName() + " " + client.getLastName();
            } else {
                return new ResponseEntity<>("Something went wrong, fam", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new Gson().toJson(name), HttpStatus.OK);
    }

    @PostMapping("/getUserId")
    public ResponseEntity<?> getCurrentUserId(@RequestHeader("Authorization") String token) {
        int userId = TokenService.getValidUserID(token);
        if (userId == 0){
            return new ResponseEntity<>("Not a valid Token", HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(userId, HttpStatus.OK);
        }
    }

    @GetMapping("/getLatestApiCallTime")
    public ResponseEntity<?> getLatestApiCallTime() {
        return ResponseEntity.ok(rootRepository.getLatestAPICallTime());
    }

    @GetMapping("/cryptoOverview")
    public ResponseEntity<?> getCryptoOverview(@RequestHeader("Authorization") String token) {
        if (!TokenService.validateJWT(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Crypto> cryptoOverview = rootRepository.getCryptoOverview();
        return ResponseEntity.ok(cryptoOverview);
    }

    /**
     * Endpoint to obtain the calculated price deltas for a certain period of time.
     * @param token JWT-token to verify authentication
     * @param dateTime selected date-time against which to compare the current price
     * @return map with key-value pair: "symbol: priceDelta".
     */
    @GetMapping("/priceDeltas")
    public ResponseEntity<?> getPriceDeltas(@RequestHeader("Authorization") String token,
                                            @RequestHeader("dateTime")
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        if (!TokenService.validateJWT(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, Double> priceDeltas = rootRepository.getPriceDeltas(dateTime);
        return ResponseEntity.ok(priceDeltas);
    }
}
