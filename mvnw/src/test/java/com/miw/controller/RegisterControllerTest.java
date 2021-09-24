/**
 * @Author: Johnny Chan, MIW student 500878034.
 * Deze class test de RegisterController en omvat de volgende 3 onderdelen:
 * -> test of de registratie van een nieuwe user succesvol verloopt
 * -> test of de registratie van een reeds bestaande user weigert
 * -> test of de validatiechecks onvolledige en/of onjuiste input van klantgegevens weigert
 */
package com.miw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miw.model.Address;
import com.miw.model.Client;
import com.miw.service.authentication.RegistrationService;
import com.miw.service.authentication.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
//@WebMvcTest(RegisterController.class) //deze annotatie vervangen met bovenste. Werkt nml niet i.c.m. de constructor in BankSinatraApplication
class RegisterControllerTest {

    private final MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistrationService registrationService;
    @MockBean
    private HashService hashService;

    @Autowired
    public RegisterControllerTest(MockMvc mockMvc) {
        super();
        this.mockMvc = mockMvc;
    }

    private Client testClient;

    @BeforeEach
    public void setup() {
        Address address = new Address("Amsterdam", "1102AB", "Lalastraat", 10, "E");
        LocalDate dateOfBirth = LocalDate.of(2000, 8, 11);
        testClient = new Client("test@test.com", "zeerveiligwachtwoord2", null, "Lala", "van", "Bobo", dateOfBirth, 123456782, address);
    }

    /**
     * Test:
     * 1. Nieuwe klantregistratie dient te slagen met als response status 201 - Created.
     * 2. ResponseEntity dient plain text te zijn.
     */
    @Test
    void registerNewClientTest() {
        //Met Mockito door validatiecheck slagen: klant bestaat niet, nieuwe klant mag dan geregistreerd worden
        Mockito.when(registrationService.checkExistingClientAccountEmail(testClient.getEmail())).thenReturn(false);

        //Test 1: de register endpoint uitvoeren waarna http-status code 201-Created verwacht wordt.
        try {
            MockHttpServletResponse response =
                    mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testClient)))
                            .andExpect(status().isCreated()) //Test 1: response dient 201 Created te zijn.
                            .andDo(print()).andReturn().getResponse();
//            System.out.println(response.getContentAsString());

            //Test 2: responseEntity is plain text
            assertThat(response.getContentType()).isEqualTo("text/plain;charset=UTF-8");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Test:
     * Reeds bestaande klant kan niet opnieuw een account aanmaken met hetzelfde email-adres.
     * Http response dient status 409-conflict te zijn.
     */
    @Test
    void registerExistingClientTest() {
        //Klant bestaat al in database (=true). Mockito db geeft dan true terug.
        Mockito.when(registrationService.checkExistingClientAccountEmail(testClient.getEmail())).thenReturn(true);
        //register endpoint uitvoeren en http-status code 409-conflict verwacht.
        try {
            mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testClient)))
                    .andExpect(status().isConflict())
                    .andDo(print());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    /**
     * Global method for registering a new client using the /register endpoint in the RegisterController class.
     *
     * @param testClient A client with one of the required fields as null or with invalid input.
     */
    public void registerTestClient(Client testClient) {
        //Er wordt niet voldaan aan de validatie-eisen, registrationService returnt dan een map met violation messages.
        Map<String, String> testMap = new TreeMap<>() {{put("dataField", "violationMessage");}};
        Mockito.when(registrationService.validateUserDetails(testClient)).thenReturn(testMap);
        try {
            mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testClient)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * VALIDATION TESTS TO DETERMINE IF EMPTY REQUIRED FIELDS FOR CLIENT REGISTRATION ARE CORRECTLY REJECTED.
     * RETURNED HTTP-STATUS CODE SHOULD BE 400-BAD REQUEST.
     */

    @Test
    void EmptyEmailRegistrationTest() {
        testClient.setEmail(null);
        registerTestClient(testClient);
    }

    @Test
    void EmptyPasswordRegistrationTest() {
        testClient.setPassword(null);
        registerTestClient(testClient);
    }

    @Test
    void EmptyFirstNameRegistrationTest() {
        testClient.setFirstName(null);
        registerTestClient(testClient);
    }

    @Test
    void EmptyLastNameRegistrationTest() {
        testClient.setLastName(null);
        registerTestClient(testClient);
    }

    @Test
    void EmptyBirthdateRegistrationTest() {
        testClient.setDateOfBirth(null);
        registerTestClient(testClient);
    }


    /**
     * VALIDATION TESTS ON CLIENT INPUT WHEN REGISTERING.
     * RETURNED HTTP-STATUS CODE SHOULD BE 400-BAD REQUEST.
     * <p>
     * -> email moet een geldige format zijn
     * ---> local part: digits 0-9, latin letters a-Z, printable characters !#$%&’*+-/=?^_`{|}~ and dot . if not initial or last character or used consecutively
     * ---> domain part: digits 0-9, latin letters a-Z, hyphen - or dot . if not initial or last character
     * -> password moet min. 8 en max. 8 karakters zijn
     * -> geboortedatum moet in het verleden liggen
     * -> bsn moet 11-proef slagen
     * -> stad moet uit letters bestaan, incl. letters met speciale karakters.
     * -> postcode moet aan NLe format voldoen: 4 cijfers (eerste cijfer mag geen 0 zijn) en 2 letters (SA|SD|SS niet toegestaan).
     * -> huisnr moet een positieve integer zijn
     * -> huisnr toevoeging moet een letter en/of getal zijn, evt. met een streepje ertussen.
     */


    @Test
    void invalidEmailTest() {
        testClient.setEmail("alice.example.com"); //missing @ character instead of .
        registerTestClient(testClient);

        testClient.setEmail("alice..bob@example.com"); //two consecutive dots not permitted
        registerTestClient(testClient);

        testClient.setEmail("alice@.example.com"); //domain cannot start with a dot.
        registerTestClient(testClient);

    }

    @Test
    void invalidPasswordTest() {
        testClient.setPassword("2kort"); //<8 characters not allowed
        registerTestClient(testClient);

        testClient.setPassword("ditwachtwoordislangerdan64karakterswaardoormensenvoorspelbarewachtwoordzinnengebruiken"); //>64 characters not allowed
        registerTestClient(testClient);
    }

    @Test
    void invalidFutureBirthdateTest() {
        testClient.setDateOfBirth(LocalDate.now().plusYears(10)); //10 jaar vanaf current date, ongeboren mensen niet welkom
        registerTestClient(testClient);
    }

    @Test
    void invalidBsnTest() {
        testClient.setBsn(123456789); //dit voldoet niet aan 11-proef
        registerTestClient(testClient);
    }

    @Test
    void invalidCityTest() {
        testClient.getAddress().setCity(" teststad"); //begint met spatie wat niet mag
        registerTestClient(testClient);

        testClient.getAddress().setCity("teststad56"); //bevat cijfers wat niet mag
        registerTestClient(testClient);
    }

    @Test
    void invalidStreetTest() {
        testClient.getAddress().setStreet("teststraat&^$%"); //invalide straat: bevat speciale karakters
        registerTestClient(testClient);
    }

    @Test
    void invalidZipCodeTest() {
        testClient.getAddress().setZipCode("12345AB"); //is 5 ipv 4 cijfers
        registerTestClient(testClient);

        testClient.getAddress().setZipCode("0234 AB"); //mag niet beginnen met 0, moet 1-9 zijn
        registerTestClient(testClient);

        testClient.getAddress().setZipCode("1106 SD"); //postcodeletters SA|SD|SA worden niet gebruikt in NL vanwege hun associatie met Nazi-Duitsland
        registerTestClient(testClient);
    }

    @Test
    void invalidHouseNrTest() {
        testClient.getAddress().setHouseNumber(-10); //alleen positieve getallen toegestaan
        registerTestClient(testClient);
    }

    @Test
    void invalidHouseNrExtensionTest() {
        testClient.getAddress().setHouseNumberExtension("-4b");//moet met letter of getal beginnen
        registerTestClient(testClient);

        testClient.getAddress().setHouseNumberExtension("b^");//mag geen vreemde karakters bevatten (m.u.v. - mits het na een letter of getal is)
        registerTestClient(testClient);
    }

}
