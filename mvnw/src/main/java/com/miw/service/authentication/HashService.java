package com.miw.service.authentication;

import com.miw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HashService {
    private static final int DEFAULT_ROUNDS = 4;
    private final PepperService pepperService;
    private final SaltMaker saltMaker;
    private int rounds;

    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    public HashService(PepperService pepperService, SaltMaker saltMaker) {
        this(pepperService, saltMaker, DEFAULT_ROUNDS);
    }

    public HashService(PepperService pepperService, SaltMaker saltMaker, int rounds) {
        this.pepperService = pepperService;
        this.saltMaker = saltMaker;
        this.rounds = rounds;
        logger.info("New HashService");
        // eventueel controleren op te grote waarden (< 6)
    }

    public User hash(User user) {
        String salt = saltMaker.generateSalt();
        user.setSalt(salt);
        String hash = HashHelper.hash(user.getPassword(), salt, pepperService.getPepper());
        user.setPassword(processRounds(hash, numberOfRounds(rounds)));
        return user;
    }

    public User hashForAuthenticate (User user) {
        String hash = HashHelper.hash(user.getPassword(), user.getSalt(), pepperService.getPepper());
        user.setPassword(processRounds(hash, numberOfRounds(rounds)));
        return user;
    }

    private String processRounds(String hash, long r) {
        for (long i = 0; i < r; i++) {
            // niet zo efficient om dit met String te doen en HashHelper hash maakt ook steeds nieuwe objecten aan
            // wordt wel al heel snel erg traag
            hash = HashHelper.hash(hash);
        }
        return hash;
    }

    // Math.pow geeft een double terug, een long is gewenst
    // om testbaar te maken, naar eigen klasse toe zetten
    private long numberOfRounds(int load){
        int base = 10;
        long result = base; // base ^ 1

        for (int i = 0; i < load; i++) {
            result *= base;
        }
        return result;
    }
}
