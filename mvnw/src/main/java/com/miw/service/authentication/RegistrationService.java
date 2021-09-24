/*
 *  @Author: Johnny Chan, MIW student 500878034.
 *  Deze service class bevat de onderliggende business logic voor de RegisterController:
 *  -> check of user reeds geregistreerd is en registratie van nieuwe users.
 */
package com.miw.service.authentication;

import com.miw.model.Administrator;
import com.miw.model.Client;
import com.miw.database.RootRepository;
import com.miw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class RegistrationService {

    private RootRepository rootRepository;

    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    public RegistrationService(RootRepository rootRepository) {
        super();
        this.rootRepository = rootRepository;
        logger.info("New RegistrationService");
    }

    public void register(User user) {
        if (user instanceof Client) {
            rootRepository.saveNewClient((Client) user);
        } else if (user instanceof Administrator) {
            rootRepository.saveNewAdmin((Administrator) user);
        }
    }

    public Map<String, String> validateUserDetails (User user) {
        Set<ConstraintViolation<User>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(user);
        System.out.println(violations);
        Map<String, String> violationsMap = new TreeMap<>();
        System.out.println(violationsMap);
        for (ConstraintViolation<User> violation : violations) {
            violationsMap.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return violationsMap;
    }

    public boolean checkExistingClientAccountEmail(String email) {
        return rootRepository.findClientByEmail(email) != null;
    }

    public boolean checkExistingClientAccountBsn(int bsn){
        return rootRepository.findClientByBsn(bsn) != null;
    }

    public boolean checkExistingAdminAccount(String email) {
        return rootRepository.findAdminByEmail(email) != null;
    }

    public RootRepository getRootRepository() {
        return rootRepository;
    }
}
