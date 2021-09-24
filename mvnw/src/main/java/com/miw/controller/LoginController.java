package com.miw.controller;
/**
 * @Author: Nijad Nazarli
 * @Description: This controller enables users to Login to their account
 */

import com.google.gson.Gson;
import com.miw.model.Credentials;
import com.miw.service.authentication.AuthenticationService;
import com.miw.service.authentication.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    private AuthenticationService authenticationService;
    private Gson gson;
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    public LoginController(AuthenticationService authenticationService, Gson gson) {
        super();
        this.authenticationService = authenticationService;
        this.gson = gson;
        logger.info("New LoginController Created");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody String credentialsAsJson) {
        Credentials credentials = gson.fromJson(credentialsAsJson, Credentials.class);
        String response = authenticationService.authenticate(credentials);
        return informLoginStatusCode(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody String token) {
        if (TokenService.validateJWT(token)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/getID")
    public int getuserID(@RequestBody String token) {
        return TokenService.getValidUserID(token);
    }

    public ResponseEntity<?> informLoginStatusCode(String response) {
        Map<String, String> responseMessage = new HashMap<>();
        if (response.equals(authenticationService.getINVALID_CREDENTIALS())){
            responseMessage.put("message", authenticationService.getINVALID_CREDENTIALS());
            return new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
        } else if (response.equals(authenticationService.getBLOCKED_USER())) {
            responseMessage.put("message", authenticationService.getBLOCKED_USER());
            return new ResponseEntity<>(responseMessage, HttpStatus.FORBIDDEN);
        }
        responseMessage.put("userRole", TokenService.getRole(response));
        responseMessage.put("token", response);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }


}
