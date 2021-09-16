package miw.controller;

import com.miw.database.JdbcTransactionDao;
import com.miw.model.Transaction;
import com.miw.service.authentication.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class AccountController {

    private JdbcTransactionDao jdbcTransactionDao;

    @Autowired
    public AccountController(JdbcTransactionDao jdbcTransactionDao) {
        this.jdbcTransactionDao = jdbcTransactionDao;
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@RequestHeader("Authorization") String token) {
        int ID = TokenService.getValidUserID(token);
        List<Transaction> transactions =  jdbcTransactionDao.getTransactionsByUserId(ID);
        return ResponseEntity.ok(transactions);
    }


    //TODO: endPoint getBalance hiernaar verplaatsen





}
