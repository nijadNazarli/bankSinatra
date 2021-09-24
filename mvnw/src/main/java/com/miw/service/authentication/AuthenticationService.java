package com.miw.service.authentication;

import com.miw.database.*;
import com.miw.model.Administrator;
import com.miw.model.Client;
import com.miw.model.Credentials;
import com.miw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author  Nijad Nazarli
 * @apiNote This service class authenticates the user
 *               based on the details entered while logging in
 */
@Service
public class AuthenticationService {

    private HashService hashService;
    private RootRepository rootRepository;
    private final String INVALID_CREDENTIALS = "Invalid credentials";
    private final String BLOCKED_USER = "User is blocked. Please contact administrator";
    private final int JWT_VALIDITY_TIME = 7400000; //2 uur geldig
    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    public AuthenticationService(HashService hs, RootRepository rootRepository) {
        super();
        this.hashService = hs;
        this.rootRepository = rootRepository;
        logger.info("New AuthenticationService created");
    }

    public String authenticate(Credentials credentials) {
        User userDatabase = rootRepository.getUserByEmail(credentials.getEmail());
        User userLoggingIn;

        if (userDatabase != null) {
            if (userDatabase instanceof Client) {
                userLoggingIn = new Client(credentials.getEmail(), credentials.getPassword());
            } else {
                userLoggingIn = new Administrator(credentials.getEmail(), credentials.getPassword());
            }
            userLoggingIn.setSalt(userDatabase.getSalt());
            String hash = hashService.hashForAuthenticate(userLoggingIn).getPassword();

            if (userDatabase.getPassword().equals(hash)) {
                if (userDatabase.isBlocked()) {
                    return BLOCKED_USER;
                }
                return TokenService.jwtBuilder(userDatabase.getUserId(),
                        userLoggingIn instanceof Client ? "client" : "admin", JWT_VALIDITY_TIME);
            }
            return INVALID_CREDENTIALS;
        }
        return INVALID_CREDENTIALS;
    }

    public String getINVALID_CREDENTIALS() {
        return INVALID_CREDENTIALS;
    }

    public String getBLOCKED_USER() {
        return BLOCKED_USER;
    }

    public HashService getHashService() {
        return hashService;
    }

    public RootRepository getRootRepository() {
        return rootRepository;
    }
}
