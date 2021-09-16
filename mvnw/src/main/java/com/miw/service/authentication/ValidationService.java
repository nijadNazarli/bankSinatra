package com.miw.service.authentication;

import com.miw.database.RootRepository;
import com.miw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    private RootRepository rootRepository;

    private final Logger logger = LoggerFactory.getLogger(ValidationService.class);

    @Autowired
    public ValidationService(RootRepository rootRepository) {
        this.rootRepository = rootRepository;
        logger.info("New ValidationService created");
    }

/*    public String validateInput (User potentialUser) {
        StringBuilder invalidFields = new StringBuilder();
        //TODO: validatiechecks schrijven, evt middels custom validator.
        return invalidFields.toString();
    }*/

    public boolean checkExistingAccount (String email) {
        return rootRepository.findByEmail(email) != null;
    }
}
