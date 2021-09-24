package com.miw.controller;

import com.google.gson.*;
import com.miw.model.Transaction;
import com.miw.service.TransactionService;
import com.miw.service.authentication.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
public class TransactionController {

    private TransactionService transactionService;
    private Gson gson;
    private final int ACCOUNTBANK = 1;

    private final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    public TransactionController(TransactionService transactionService){
        super();
        this.transactionService = transactionService;
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                (jsonElement, type, jsonDeserializationContext) ->
                        LocalDateTime.parse(jsonElement.getAsJsonPrimitive().getAsString())).create();
        logger.info("New TransactionController-object created");
    }

    //TODO: Methode verder opschonen
    @PostMapping("/buy")
    public ResponseEntity<?> doTransaction(@RequestHeader("Authorization") String token,
                                           @RequestBody String transactionAsJson){

        if (!TokenService.validateJWT(token)) {
            return new ResponseEntity<>("Invalid login credentials, try again", HttpStatus.UNAUTHORIZED);
        }

        Transaction transaction = gson.fromJson(transactionAsJson, Transaction.class);
        int userId = TokenService.getValidUserID(token);
        int accountId = transactionService.getAccountByUserId(userId).getAccountId();

        if(!authorizedTransaction(accountId, transaction)){
            return new ResponseEntity<>("You are not authorized to purchase assets for another client," +
                    " stop it", HttpStatus.UNAUTHORIZED);
        }

        if(transaction.getUnits() < 0){
            return new ResponseEntity<>("Buyer cannot purchase negative asssets. " +
                    "Transaction cannot be completed.", HttpStatus.CONFLICT);
        }

        calculateCosts(transaction);

        if(!checkSufficientCrypto(transaction)){
            return new ResponseEntity<>("Seller has insufficient assets. Transaction cannot be completed.",
                    HttpStatus.CONFLICT);
        } else if(!checkSufficientBalance(transaction)){
            return new ResponseEntity<>("Buyer has insufficient funds. Transaction cannot be completed.",
                    HttpStatus.CONFLICT);
        }

        transfer(transaction);

        transactionService.registerTransaction(transaction);
        return new ResponseEntity<>("Joepie de poepie, transactie gedaan", HttpStatus.OK);
    }

    private boolean authorizedTransaction (int userId, Transaction transaction){
        if (userId == transaction.getBuyer()) {
            return true;
        }
        return userId == transaction.getSeller() && transaction.getBuyer() == ACCOUNTBANK;
    }

    private Transaction calculateCosts(Transaction transaction){
        transaction = setPrice(transaction);
        transaction = setCosts(transaction);
        return transaction;
    }

    private void transfer(Transaction transaction){
        transactionService.transferBalance(transaction.getSeller(), transaction.getBuyer(),
                transaction.getTransactionPrice());
        transactionService.transferCrypto(transaction.getSeller(), transaction.getBuyer(),
                transaction.getCrypto(), transaction.getUnits());
        transactionService.transferBankCosts(transaction.getSeller(), transaction.getBuyer(),
                transaction.getBankCosts());
    }

    private Transaction setPrice(Transaction transaction){
        return transactionService.setTransactionPrice(transaction);
    }

    private Transaction setCosts(Transaction transaction){
        return transactionService.setBankCosts(transaction);
    }

    private boolean checkSufficientCrypto(Transaction transaction){
        try{
            return transactionService.checkSufficientCrypto(transaction.getBuyer(), transaction.getSeller(),
                    transaction.getCrypto(), transaction.getUnits());
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkSufficientBalance(Transaction transaction){
        return transactionService.checkSufficientBalance(transaction.getSeller(), transaction.getBuyer(),
                transaction.getTransactionPrice(), transaction.getBankCosts());
    }
}
