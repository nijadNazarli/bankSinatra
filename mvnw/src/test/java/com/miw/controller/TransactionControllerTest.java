package com.miw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miw.model.Asset;
import com.miw.model.Credentials;
import com.miw.model.Crypto;
import com.miw.model.Transaction;
import com.miw.service.TransactionService;
import com.miw.service.authentication.AuthenticationService;
import com.miw.service.authentication.TokenService;
import org.apache.el.parser.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class TransactionControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private TokenService tokenService;

    @Autowired
    public TransactionControllerTest(MockMvc mockMvc){
        super();
        this.mockMvc = mockMvc;
    }

    private Credentials credentials;
    private Transaction testTransaction;
    private Crypto testCrypto;
    private Asset testAsset;
    private String testToken;

    @BeforeEach
    public void setup(){
        testCrypto = new Crypto("TestCrypto", "TCR", "It's cryptocurrency just for testing!", 400.00);
        testAsset = new Asset(testCrypto, 20, 10, 420.69);
        testAsset.setAccountId(3);
        testTransaction = new Transaction(2, 3, testCrypto, 3);
        testTransaction.setBankCosts(10.00);
        generateValidCredentials();
    }

    private void generateValidCredentials(){
        credentials = new Credentials("test1@test.com", "zeerveiligwachtwoord2");
        Mockito.when(authenticationService.authenticate(credentials))
                .thenReturn(testToken = tokenService.jwtBuilder(2, "client", 7400000));
    }

    @Test
    void validTransactionTest(){
        Mockito.when(transactionService.checkSufficientCrypto(testTransaction.getBuyer(), testTransaction.getSeller(),
                testTransaction.getCrypto(), testTransaction.getUnits())).thenReturn(true);
        Mockito.when(transactionService.checkSufficientBalance(testTransaction.getSeller(),
                testTransaction.getBuyer(), testTransaction.getTransactionPrice(), testTransaction.getBankCosts()))
                .thenReturn(true);

        try {
            MockHttpServletResponse response =
                    mockMvc.perform(post("/buy")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header(testToken)
                                    .content(objectMapper.writeValueAsString(testTransaction)))
                            .andExpect(status().isOk())
                            .andDo(print()).andReturn().getResponse();
            assertThat(response.getContentType()).isEqualTo("text/plain;charset=UTF-8");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void invalidLoginCredentialsTest(){
        testToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiIyIiwidXNlcnJvbGUiOiJjbGllbnQiLCJleHAiOjE2MzE5Nzg5OTUsImlhdCI6MTYzMTk3MTU5NX0" +
                ".Q1W8OTOQeylcPn2am7tI733gZcR7yE48v-hHqyj_c2k";
        try {
            mockMvc.perform(post("/buy")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(testToken)
                            .content(objectMapper.writeValueAsString(testTransaction)))
                    .andExpect(status().isConflict())
                    .andDo(print());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void userNotBuyerTest(){
        Transaction testTransaction2 = new Transaction (4, 5, testCrypto, 5);
        try {
            mockMvc.perform(post("/buy")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(testToken)
                            .content(objectMapper.writeValueAsString(testTransaction2)))
                    .andExpect(status().isConflict())
                    .andDo(print());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void negativeCryptoTest(){
        Transaction testTransaction3 = new Transaction(1, 2, testCrypto, -3);
        try {
            mockMvc.perform(post("/buy")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(testToken)
                            .content(objectMapper.writeValueAsString(testTransaction3)))
                    .andExpect(status().isConflict())
                    .andDo(print());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void insufficientCryptoTest(){
        Mockito.when(transactionService.checkSufficientCrypto(testTransaction.getBuyer(), testTransaction.getSeller(),
                testTransaction.getCrypto(), testTransaction.getUnits())).thenReturn(false);

        try {
            mockMvc.perform(post("/buy")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(testToken)
                            .content(objectMapper.writeValueAsString(testTransaction)))
                    .andExpect(status().isConflict())
                    .andDo(print());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void insufficientBalanceTest(){
        Mockito.when(transactionService.checkSufficientCrypto(testTransaction.getBuyer(), testTransaction.getSeller(),
                testTransaction.getCrypto(), testTransaction.getUnits())).thenReturn(true);
        Mockito.when(transactionService.checkSufficientBalance(testTransaction.getSeller(),
                testTransaction.getBuyer(), testTransaction.getTransactionPrice(),
                testTransaction.getBankCosts())).thenReturn(false);

        try {
            mockMvc.perform(post("/buy")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(testToken)
                            .content(objectMapper.writeValueAsString(testTransaction)))
                    .andExpect(status().isConflict())
                    .andDo(print());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}