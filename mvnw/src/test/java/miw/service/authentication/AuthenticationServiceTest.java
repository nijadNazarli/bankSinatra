package miw.service.authentication;

import com.miw.model.Credentials;
import com.miw.service.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthenticationServiceTest {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationServiceTest.class);
    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationServiceTest(AuthenticationService as) {
        super();
        this.authenticationService = as;
        logger.info("New AuthenticationService Integration Test");
    }

    @Test
    void integrationTest() {
        assertThat(authenticationService.getHashService()).isNotNull();
        assertThat(authenticationService.getTokenService()).isNotNull();
        assertThat(authenticationService.getClientDao()).isNotNull();
        assertThat(authenticationService.getJdbcTokenDao()).isNotNull();
    }

    @Test
    void authenticateTest() {
        Credentials validCredentials = new Credentials("test@test.com", "zeerveiligwachtwoord2");
        Credentials noArgsCredentials = new Credentials();
        Credentials oneMoreCredentials = new Credentials("an@gmail.com", "WWwoord123");
        Credentials notAnEmail = new Credentials("dd", "PASSWORD123");
        Credentials shortPassword = new Credentials("nn@gmail.com", "11");

        assertThat(authenticationService.authenticate(validCredentials)).isNotEmpty();
        assertThat(authenticationService.authenticate(noArgsCredentials)).isNullOrEmpty();
        assertThat(authenticationService.authenticate(oneMoreCredentials)).isNullOrEmpty();
        assertThat(authenticationService.authenticate(notAnEmail)).isNullOrEmpty();
        assertThat(authenticationService.authenticate(shortPassword)).isNullOrEmpty();


    }
}