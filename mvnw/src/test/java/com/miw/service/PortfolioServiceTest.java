package com.miw.service;

import com.miw.model.Asset;
import com.miw.model.Crypto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(PortfolioService.class)
class PortfolioServiceTest {

    @MockBean
    PortfolioService portfolioService;

//    @Test
//    void calculateDeltaDayValue() {
//        Crypto crypto = new Crypto("Bitcoin", "BTC", "description", 42162.03);
//        crypto.setCryptoId(1);
//        Asset asset = new Asset(crypto, 100);
//
//        double result = portfolioService.calculateDelta1DayValue(asset, 1);
//        System.out.println(result);
//    }
}