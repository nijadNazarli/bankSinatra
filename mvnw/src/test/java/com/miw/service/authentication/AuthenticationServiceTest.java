package com.miw.service.authentication;

import com.miw.model.Credentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;
/**
 *@author Nijad Nazarli
 */

@SpringBootTest
class AuthenticationServiceTest {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationServiceTest.class);
    private AuthenticationService authenticationService;
    private Credentials validCredentials;
    private Credentials noArgsCredentials;
    private Credentials oneMoreCredentials;
    private Credentials notAnEmail;
    private Credentials shortPassword;

    @Autowired
    public AuthenticationServiceTest(AuthenticationService as) {
        super();
        this.authenticationService = as;
        logger.info("New AuthenticationService Integration Test");
    }

    @Test
    void integrationTest() {
        assertThat(authenticationService.getHashService()).isNotNull();
        assertThat(authenticationService.getRootRepository()).isNotNull();
    }

    @BeforeEach
    void setUp() {
         validCredentials = new Credentials("test@test.com", "zeerveiligwachtwoord2");
         noArgsCredentials = new Credentials();
         oneMoreCredentials = new Credentials("an@gmail.com", "WWwoord123");
         notAnEmail = new Credentials("dd", "PASSWORD123");
         shortPassword = new Credentials("nn@gmail.com", "11");
    }

    @Test
    void authenticateTest(){
        assertThat(authenticationService.authenticate(validCredentials)).isNotEmpty();
        assertThat(authenticationService.authenticate(noArgsCredentials)).isEqualTo(authenticationService.getINVALID_CREDENTIALS());
        assertThat(authenticationService.authenticate(oneMoreCredentials)).isEqualTo(authenticationService.getINVALID_CREDENTIALS());
        assertThat(authenticationService.authenticate(notAnEmail)).isEqualTo(authenticationService.getINVALID_CREDENTIALS());
        assertThat(authenticationService.authenticate(shortPassword)).isEqualTo(authenticationService.getINVALID_CREDENTIALS());
    }
}