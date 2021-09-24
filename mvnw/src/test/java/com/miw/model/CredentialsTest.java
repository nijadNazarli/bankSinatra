package com.miw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
/**
 *@author Nijad Nazarli
 */

class CredentialsTest {
    private Credentials validCredentials;
    private Credentials noArgsCredentials ;
    private Credentials identicalCredentials;
    private Credentials oneMoreCredentials;
    private Credentials notAnEmail;
    private Credentials shortPassword;
    private Credentials blankCredentials;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        validCredentials = new Credentials("nn@gmail.com", "ThisIsAPW&");
        noArgsCredentials = new Credentials();
        identicalCredentials = new Credentials("nn@gmail.com", "ThisIsAPW&");
        oneMoreCredentials = new Credentials("an@gmail.com", "WWwoord123");
        notAnEmail = new Credentials("dd", "PASSWORD123");
        shortPassword = new Credentials("nn@gmail.com", "11");
        blankCredentials = new Credentials("", "");
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        validator = vf.getValidator();
    }

    @Test
    void testWrongEmail() {
        Set<ConstraintViolation<Credentials>> violations = validator.validate(notAnEmail);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testShortPassword() {
        Set<ConstraintViolation<Credentials>> violations2 = validator.validate(shortPassword);
        assertFalse(violations2.isEmpty());
    }

    @Test
    void testEmptyCredentials() {
        Set<ConstraintViolation<Credentials>> violation3 = validator.validate(blankCredentials);
        assertFalse(violation3.isEmpty());
    }


    @Test
    void testNoArgsConstructor() {
        assertThat(noArgsCredentials.getPassword()).isNullOrEmpty();
        assertThat(noArgsCredentials.getEmail()).isNullOrEmpty();
    }

    @Test
    void testNotMatchingCredentials() {
        assertThat(validCredentials).isNotEqualTo(oneMoreCredentials);
    }

    @Test
    void testIdenticalCredentials() {
        assertThat(validCredentials).isEqualTo(identicalCredentials);
    }
}