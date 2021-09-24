package com.miw.service;

import com.miw.database.RootRepository;
import com.miw.model.Account;
import com.miw.service.authentication.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private RootRepository rootRepository;
    private final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    public AccountService(RootRepository rootRepository){
        super();
        this.rootRepository = rootRepository;
        logger.info("New AccountService");
    }

    public Account getAccountByUserId(int userId){
        return rootRepository.getAccountByUserId(userId);
    }
}
