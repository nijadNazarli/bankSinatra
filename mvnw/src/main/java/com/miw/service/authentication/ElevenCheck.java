package com.miw.service.authentication;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ElevenCheckValidation.class)
public @interface ElevenCheck {
    String message() default "Number is invalid, does not pass the eleven check";

    Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default{};

}
