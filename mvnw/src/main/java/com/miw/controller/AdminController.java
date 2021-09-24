package com.miw.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.miw.database.*;
import com.miw.model.*;
import com.miw.service.authentication.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
public class AdminController {

    private final double MAX_FEE = 1;
    private final double MIN_FEE = 0;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private RootRepository rootRepository;

    @Autowired
    public AdminController(RootRepository rootRepository) {
        super();
        this.rootRepository = rootRepository;
        logger.info("New AdminController created");
    }

    @PutMapping("/admin/updateFee")
    public ResponseEntity<?> updateFee(@RequestBody String json) {
        JsonObject convertedObject = new Gson().fromJson(json, JsonObject.class);
        String token = convertedObject.get("token").getAsString();
        double fee = convertedObject.get("fee").getAsDouble();
        if (TokenService.validateAdmin(token)) {
            if (fee >= MIN_FEE && fee <= MAX_FEE) { // also checked at frontend; here just in case
                rootRepository.updateBankCosts(fee);
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/admin/getBankFee")
    public ResponseEntity<?> getBankFee(@RequestHeader("Authorization") String token) {
        if (TokenService.validateAdmin(token)) {
            return ResponseEntity.ok(rootRepository.getBankCosts());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/admin/getUserData")
    public ResponseEntity<?> getUserData(@RequestHeader("Authorization") String token, @RequestParam String email) {
        User user = rootRepository.getUserByEmail(email);

        if (user instanceof Client) {
            user = rootRepository.findClientByEmail(user.getEmail());
        } else if (user instanceof Administrator) {
            user = rootRepository.findAdminByEmail(user.getEmail());
        }

        user.setSalt(null); user.setPassword(null); // remove data that don't need to be shown on the front-end
        if (TokenService.validateAdmin(token)) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/admin/toggleBlock")
    public ResponseEntity<?> toggleBlock(@RequestHeader("Authorization") String token, @RequestParam String email) {
        User user = rootRepository.getUserByEmail(email);
        if (TokenService.validateAdmin(token)) {
            rootRepository.toggleBlock(!user.isBlocked(), user.getUserId()); // block toggle through inversion of initial block status
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/admin/getAssets")
    public ResponseEntity<?> getAssets(@RequestHeader("Authorization") String token, @RequestParam String email) {
        Account account = rootRepository.getAccountByEmail(email);
        Map<String, Double> assets = new TreeMap<>(); // TreeMap causes alphabetical ordering on front-end.

        if (TokenService.validateAdmin(token)) {
            assets.put("USD", rootRepository.getBalanceByEmail(email)); // USD balance is not stored with the crypto assets.
            List<Crypto> allCryptos = rootRepository.getAllCryptos();
            for (Crypto crypto : allCryptos) {
                Asset asset = rootRepository.getAssetBySymbol(account.getAccountId(), crypto.getSymbol());
                if (asset != null) { // if the asset fetch from db returned anything, we can show its quantity, otherwise zero
                    assets.put(crypto.getSymbol(), asset.getUnits());
                } else {
                    assets.put(crypto.getSymbol(), 0.0);
                }
            }
            return ResponseEntity.ok(assets);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/admin/updateAssets")
    public ResponseEntity<?> updateAssets(@RequestHeader("Authorization") String token, @RequestParam String email, @RequestBody String json) {
        if (TokenService.validateAdmin(token)) {
            JsonObject changes = new Gson().fromJson(json, JsonObject.class);

            Account account = rootRepository.getAccountByEmail(email);
            rootRepository.updateBalance(account.getBalance() + changes.get("USD").getAsDouble(),
                    account.getAccountId()); // update balance, which is not stored in the same db table as cryptos

            List<Crypto> allCryptos = rootRepository.getAllCryptos();
            for (Crypto crypto : allCryptos) {
                double unitsChange = changes.get(crypto.getSymbol()).getAsDouble();
                updateCrypto(crypto, unitsChange, account.getAccountId());
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Hulpmethoden:
    public void updateCrypto(Crypto crypto, double unitsChange, int accountId) {
        String symbol = crypto.getSymbol();
        Asset asset = rootRepository.getAssetBySymbol(accountId, symbol);

        if (asset != null && unitsChange != 0) {    // only call dao if there is an actual # change
            double newUnits = asset.getUnits() + unitsChange;
            if (newUnits > 0) {                     // if/else here prevents an extant asset units # going negative
                rootRepository.updateAsset(newUnits, symbol, accountId);
            } else {                                // if new value would be negative, set to 0
                rootRepository.updateAsset(0, symbol, accountId);
            }
        } else if (unitsChange > 0) {               // if an asset !exist and new value != negative, save to db
            rootRepository.saveAsset(accountId, symbol, unitsChange);
        }
    }
}
