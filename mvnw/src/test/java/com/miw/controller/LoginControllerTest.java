package com.miw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miw.model.Credentials;
import com.miw.service.authentication.AuthenticationService;
import com.miw.service.authentication.TokenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *@author Nijad Nazarli
 */

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    private Logger logger = LoggerFactory.getLogger(LoginControllerTest.class);
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    public LoginControllerTest(MockMvc mockMvc) {
        super();
        this.mockMvc = mockMvc;
        logger.info("New LoginController Test");
    }

    @Test
    void loginUser() {
        Credentials validCredentials = new Credentials("test1@test.com", "zeerveiligwachtwoord2");

        Date futureDate = new Date(2025, Calendar.JANUARY, 1, 0, 10, 10);
        long futureMilSec = futureDate.getTime();
        String token = TokenService.jwtBuilderSetDate(1, "client",
                futureMilSec, 600000);

        Map<String, String> loginResponse = new HashMap<>();
        loginResponse.put("userRole", "client");
        loginResponse.put("token", token);

        Mockito.when(authenticationService.authenticate(validCredentials)).thenReturn(token);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/login");
        request.contentType(MediaType.APPLICATION_JSON).content(asJsonString(validCredentials));
        try {

            ResultActions actions = mockMvc.perform(request);
            MockHttpServletResponse response = actions.andExpect(status().isOk()).andDo(print()).andReturn().getResponse();

            assertThat(response.getContentAsString()).isNotEmpty();
            assertThat(response.getContentType()).isEqualTo("application/json");
            assertThat(response.getContentAsString()).isEqualTo(asJsonString(loginResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}