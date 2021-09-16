package com.miw.controller;

import com.miw.model.Client;
import com.miw.service.authentication.HashService;
import com.miw.service.RegistrationService;
import com.miw.service.authentication.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private RegistrationService registrationService;
    private ValidationService validationService;
    private HashService hashService;

    private final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    public RegisterController(RegistrationService registrationService, ValidationService validationService, HashService hashService){
        super();
        this.registrationService = registrationService;
        this.validationService = validationService;
        this.hashService = hashService;
        logger.info("New RegisterController-object created");
    }


    @PutMapping
    public ResponseEntity<?> registerUser(@Valid @RequestBody Client client){
        //Validatie volledigheid en juiste format van input zijn in de domeinklassen zelf gebouwd.
        //Check of klant al bestaat in de database.
        if (validationService.checkExistingAccount(client.getEmail())) {
            return ResponseEntity.unprocessableEntity().body("Registration failed. Account already exists.");
        }
        // Gebruiker opslaan in database en beginkapitaal toewijzen. Succesmelding geven.
        client = hashService.hash(client);
        registrationService.register(client);
        return new ResponseEntity<>("User successfully registered. Welcome to Bank Sinatra!", HttpStatus.CREATED);
    }
}
