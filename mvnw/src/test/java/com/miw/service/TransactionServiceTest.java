package com.miw.service;

import com.miw.database.RootRepository;
import com.miw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
class TransactionServiceTest {

    RootRepository mockRepo = Mockito.mock(RootRepository.class);
    TransactionService transactionService = new TransactionService(mockRepo);

    private Account testSellerAccount;
    private Account testBuyerAccount;
    private Bank testBank;
    private Crypto testCrypto;
    private Asset testAsset;
    private Transaction testTransaction;

    public TransactionServiceTest(){
        super();
    }

    @BeforeEach
    public void setup(){
        //TODO: tests werkende krijgen met Bank
        testBank = Bank.getBankSinatra();
        testSellerAccount = new Account();
        testSellerAccount.setAccountId(2);
        testBuyerAccount = new Account();
        testBuyerAccount.setAccountId(3);

        testCrypto = new Crypto("TestCrypto", "TCR", "It's cryptocurrency just for testing!", 500.00);
        testAsset = new Asset(testCrypto, 20, 10, 420.69);
        testAsset.setAccountId(3);

        Mockito.when(mockRepo.getAccountById(1)).thenReturn(testBank.getAccount());
        Mockito.when(mockRepo.getAccountById(2)).thenReturn(testSellerAccount);
        Mockito.when(mockRepo.getAccountById(3)).thenReturn(testBuyerAccount);
        Mockito.when(mockRepo.getAssetBySymbol(3, "TCR")).thenReturn(testAsset);
        Mockito.when(mockRepo.getBankCosts()).thenReturn(0.01);

        testTransaction = new Transaction(2, 3, testCrypto, 3);
        transactionService.setTransactionPrice(testTransaction);
        transactionService.setBankCosts(testTransaction);
    }

    @Test
    void setTransactionPriceTest(){
        double expected = testAsset.getSalePrice() * 3;
        double actual = transactionService.setTransactionPrice(testTransaction).getTransactionPrice();
        assertEquals(expected, actual);
    }

    @Test
    void checkSufficientBalance1(){
        assertTrue(transactionService.checkSufficientBalance(2, 3, testTransaction.getTransactionPrice(),
                testTransaction.getBankCosts()));
    }

    @Test
    void checkInsufficientBalance(){
        assertFalse(transactionService.checkSufficientBalance(2, 3, 1000000,
                testTransaction.getBankCosts()));
    }

    @Test
    void checkInsufficientBalanceForBankCosts(){
        testTransaction.setBankCosts(100);
        assertFalse(transactionService.checkSufficientBalance(2, 3,
                testBuyerAccount.getBalance() - 1, testTransaction.getBankCosts()));
    }

    @Test
    void getPortfolioStats() {
    }
}