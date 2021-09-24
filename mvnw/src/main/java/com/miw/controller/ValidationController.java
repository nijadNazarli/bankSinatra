package com.miw.controller;

import com.miw.database.RootRepository;
import com.miw.service.authentication.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class ValidationController {

    private final Logger logger = LoggerFactory.getLogger(ValidationController.class);
    private RootRepository rootRepository;

    @Autowired
    public ValidationController(RootRepository rootRepository) {
        super();
        this.rootRepository = rootRepository;
        logger.info("New ValidationController-object created");
    }

    // Validates if user is a client. Use before endpoints to grand access to right html pages.
    @PostMapping("/validateClient")
    public ResponseEntity<?> validateClient(@RequestBody String token) {
        if (TokenService.validateClient(token)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        if (TokenService.validateAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // TODO: terug naar login paginga
        // TODO: terug naar pagina van voorheen
        // url opslaan in de body???
    }

    // Validates if user is an admin. Use before loading relevant html page.
    @PostMapping("/validateAdmin")
    public ResponseEntity<?> validateAdmin(@RequestBody String token) {
        if (TokenService.validateAdmin(token)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        if (TokenService.validateClient(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    // Validates if user is legitimate and logged in. Use before loading relevant html page.
    @PostMapping("/validateUser")
    public ResponseEntity<?> validateUser(@RequestBody String token) {
        if (TokenService.validateJWT(token)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Returns name of validated user.
    @GetMapping("/getNameUser")
    public ResponseEntity<?> getNameByUserId(@RequestHeader("Authorization") String token){
        int userId = TokenService.getValidUserID(token);
        if (userId == 0) {
            return new ResponseEntity<>("Not a valid Token", HttpStatus.UNAUTHORIZED);
        } else {
            String name = rootRepository.getFirstNameById(userId);
            return new ResponseEntity<>(name, HttpStatus.OK);
        }
    }
}
