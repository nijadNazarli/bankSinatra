package com.miw.service.authentication;
/**
 * @Author: Nijad Nazarli
 * @Description: This service class authenticates the user
 *               based on the details entered while logging in
 */
import com.miw.database.JdbcTokenDao;
import com.miw.database.JdbcClientDao;
import com.miw.model.Client;
import com.miw.model.Credentials;
import com.miw.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private HashService hashService;
    private JdbcClientDao clientDao;
    private TokenService tokenService;
    private JdbcTokenDao jdbcTokenDao;

    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    public AuthenticationService(HashService hs, JdbcClientDao clientDao, TokenService tokenService, JdbcTokenDao jdbcTokenDao) {
        super();
        this.hashService = hs;
        this.clientDao = clientDao;
        this.tokenService = tokenService;
        this.jdbcTokenDao = jdbcTokenDao;
        logger.info("New AuthenticationService created");
    }

    public String authenticate(Credentials credentials) {
        Client clientDatabase = clientDao.findByEmail(credentials.getEmail());
        Client clientLogIn = new Client(credentials.getEmail(), credentials.getPassword());
        String hash;

        if (clientDatabase != null) {
            clientLogIn.setSalt(clientDatabase.getSalt());
            hash = hashService.hashForAuthenticate(clientLogIn).getPassword();

            if (clientDatabase.getPassword().equals(hash)) {
                String token = tokenService.jwtBuilder(credentials.getEmail().toString(),7400000); //2 uur geldig
                return token;
            }
        }
        return "";
    }

    public HashService getHashService() {
        return hashService;
    }

    public JdbcClientDao getClientDao() {
        return clientDao;
    }

    public TokenService getTokenService() {
        return tokenService;
    }

    public JdbcTokenDao getJdbcTokenDao() {
        return jdbcTokenDao;
    }
}
