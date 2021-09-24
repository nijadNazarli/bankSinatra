package com.miw.controller;

import com.miw.model.Account;
import com.miw.model.Transaction;
import com.miw.service.AccountService;
import com.miw.service.TransactionService;
import com.miw.service.authentication.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class AccountController {

    private TransactionService transactionService;
    private AccountService accountService;

    @Autowired
    public AccountController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    @GetMapping("/transactionsBuyer")
    public ResponseEntity<?> getTransactionsBuyer(@RequestHeader("Authorization") String token) {
        int userId = TokenService.getValidUserID(token);
        List<Transaction> transactions =  transactionService.getTransactionsBuyer(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactionsSeller")
    public ResponseEntity<?> getTransactionsSeller(@RequestHeader("Authorization") String token) {
        int userId = TokenService.getValidUserID(token);
        List<Transaction> transactions =  transactionService.getTransactionsSeller(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/getAccount")
    public ResponseEntity<?> getAccount(@RequestHeader("Authorization") String token){
        int userId = TokenService.getValidUserID(token);
        Account account = accountService.getAccountByUserId(userId);
        if (account == null){
            return new ResponseEntity<>("Something's going wrong here", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
    }


    //TODO: endPoint getBalance hiernaar verplaatsen





}
