package miw.model;

import com.miw.model.Credentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
    void testWrongInput() {
        Set<ConstraintViolation<Credentials>> violations = validator.validate(notAnEmail);
        Set<ConstraintViolation<Credentials>> violations2 = validator.validate(shortPassword);
        Set<ConstraintViolation<Credentials>> violation3 = validator.validate(blankCredentials);
        assertFalse(violations.isEmpty());
        assertFalse(violations2.isEmpty());
        assertFalse(violation3.isEmpty());
    }

    @Test
    void testCredentialsOnEquality() {
        assertThat(validCredentials).isEqualTo(identicalCredentials);
        assertThat(validCredentials).isNotEqualTo(oneMoreCredentials);
        assertThat(noArgsCredentials.getEmail()).isNullOrEmpty();
        assertThat(noArgsCredentials.getPassword()).isNullOrEmpty();
    }
}