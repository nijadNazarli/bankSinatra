package com.miw.service.authentication;

import org.junit.jupiter.api.Test;
import java.util.Calendar;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;


class TokenServiceTest {

    private String setUpCurrentJWT(){
        return TokenService.jwtBuilderSetDate(1, "client",
                System.currentTimeMillis(), 600000); //10 min
    }


    // set up valid jwt with future date (so it will not expire)
    private String setUpValidJWT(){
        Date futureDate = new Date(2025, Calendar.JANUARY, 1, 0, 10, 10);
        long futureMilSec = futureDate.getTime();
        return TokenService.jwtBuilderSetDate(1, "admin",
                futureMilSec, 600000); //10 min
    }

    // Set up invalid jwt with expiration date of zero.
    private String setUpInvalidJWT(){
        return TokenService.jwtBuilderSetDate(1, "admin",
                new Date().getTime(), 0); //0 min
    }


    @Test
    void jwtBuilder() {
        String actualJwt = setUpValidJWT();
        String expectedJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiIxIiwidXNlcnJvbGUiOiJhZG1pbiIsImV4cCI6NjE2OTM4MzEyMTAsImlhdCI6NjE2OTM4MzA2MTB9" +
                ".akomb9MkjpLFaM1EUFrbBfrP9c3jMLPa9tkpx2kDEGU";
        assertEquals(expectedJwt, actualJwt);
    }

    @Test
    void decodeJWTBoolExpired() {
        Boolean actual = TokenService.validateJWT(setUpInvalidJWT());
        Boolean expected = false;
        assertEquals(expected, actual);
    }

    @Test
    void decodeJWTBoolValid() {
        Boolean actual = TokenService.validateJWT(setUpValidJWT());
        Boolean expected = true;
        assertEquals(expected, actual);
    }


    @Test
    void decodeJwt() {
        String actualClaim = TokenService.decodeJWT(setUpValidJWT()).toString();
        String expectedClaim = "{sub=1, userrole=admin, exp=61693831210, iat=61693830610}";
        assertEquals(expectedClaim, actualClaim);
    }


    @Test
    void validateAndGetID() {
        int actual = TokenService.getValidUserID(setUpValidJWT());
        int expected = 1;
        assertEquals(expected, actual);
    }

//    @Test
//    void getIDExpiredJWT() {
//        int actual = TokenService.getValidUserID(setUpInvalidJWT());
//        int expected = null;
//        assertEquals(expected, actual);
//    }

    @Test
    void getRole() {
        String actual = TokenService.getRole(setUpValidJWT());
        String expected = "admin";
        assertEquals(expected, actual);
    }

    @Test
    void validateAdmin() {
        Boolean actual = TokenService.validateAdmin(setUpValidJWT());
        Boolean expected = true;
        assertEquals(expected, actual);
    }

    @Test
    void validateClient() {
        Boolean actual = TokenService.validateClient(setUpValidJWT());
        Boolean expected = false;
        assertEquals(expected, actual);
    }

    @Test
    void testCurrentJwtBuilderSubject() {
        String actual = TokenService.decodeJWT(setUpCurrentJWT()).getSubject();
        String expected = "1";
        assertEquals(expected, actual);

    }

    @Test
    void testCurrentJwtBuilderRole() {
        String actual= TokenService.decodeJWT(setUpCurrentJWT()).get("userrole").toString();
        String expected = "client";
        assertEquals(expected, actual);

    }


}