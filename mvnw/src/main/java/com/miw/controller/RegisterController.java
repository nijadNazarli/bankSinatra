/**
 * @Author: Johnny Chan en Renée Jansen.
 * Deze class mapt de /register endpoints voor nieuwe klanten en admins.
 */
package com.miw.controller;

import com.google.gson.*;
import com.miw.model.Administrator;
import com.miw.model.Client;
import com.miw.service.authentication.HashService;
import com.miw.service.authentication.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

@RestController
@Validated
public class RegisterController {

    private RegistrationService registrationService;
    private HashService hashService;
    private Gson gson;

    private final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    public RegisterController(RegistrationService registrationService, HashService hashService) {
        super();
        this.registrationService = registrationService;
        this.hashService = hashService;
        logger.info("New RegisterController-object created");
        gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return LocalDate.parse(jsonElement.getAsJsonPrimitive().getAsString());
            }
        }).create();
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerClient(@RequestBody String clientDetails) {
        Client client = gson.fromJson(clientDetails, Client.class);
        //Validatie volledigheid en juiste format van input. Validatie-eisen staan bij de attributen in de domeinklassen zelf.
        Map<String, String> violationsMap = registrationService.validateUserDetails(client);
//        System.out.println("violations zijn: " + violationsMap);
        if (!violationsMap.isEmpty()) {
            return ResponseEntity.unprocessableEntity().body(violationsMap);
        }
        //Check of klant al bestaat in de database.
        if (registrationService.checkExistingClientAccountEmail(client.getEmail())) {
            return new ResponseEntity<>("Registration failed. An account with this e-mail address already exists.", HttpStatus.CONFLICT);
        } else if (registrationService.checkExistingClientAccountBsn(client.getBsn())){
            return new ResponseEntity<>("Registration failed. An account with this bsn already exists.", HttpStatus.CONFLICT);
        }
        //Gebruiker opslaan in database en beginkapitaal toewijzen. Succesmelding geven.
        client = (Client) hashService.hash(client);
        registrationService.register(client);
        return new ResponseEntity<>("User successfully registered. Welcome to Bank Sinatra!", HttpStatus.CREATED);
    }

    @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(@RequestBody String adminDetails) {
        Administrator admin = gson.fromJson(adminDetails, Administrator.class);
        Map<String, String> violationsMap = registrationService.validateUserDetails(admin);
        if (!violationsMap.isEmpty()) {
            return new ResponseEntity<>(violationsMap, HttpStatus.BAD_REQUEST);
        }
        //Check of admin-account reeds bestaat in de database.
        if (registrationService.checkExistingAdminAccount(admin.getEmail())) {
            return new ResponseEntity<>("Registration failed. Admin account already exists.", HttpStatus.CONFLICT);
        }
        //Admin opslaan in de database met een blocked status.
        admin = (Administrator) hashService.hash(admin);
        registrationService.register(admin);
        return new ResponseEntity<>("Your request for an administrator account has been received and is pending " +
                "further approval. \nFor inquiries, please contact your Manager or IT Supervisor.", HttpStatus.CREATED);
    }
}
