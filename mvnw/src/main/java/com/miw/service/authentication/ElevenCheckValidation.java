package com.miw.service.authentication;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.LinkedList;
import java.util.List;

/*
@author Renée
Eleven check for Dutch social security numbers (BSN).
A valid BSN consists of 8 or 9 digits and passes a slightly altered version of the Modulo 11 test,
with the difference that the last value is multiplied by -1 instead of 1.
 */

public class ElevenCheckValidation implements ConstraintValidator<ElevenCheck, Integer> {

    @Override
    public void initialize(ElevenCheck constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer bsn, ConstraintValidatorContext constraintValidatorContext) {
        List<Integer> digits = makeDigits(bsn);
        if (digits.size() < 8 || digits.size() > 9){
            return false;
        }
        int sum = elevenTestSum(digits);
        return sum%11 == 0;
    }

    public List<Integer> makeDigits(Integer fullNr){
        List<Integer> digits = new LinkedList<>();

        //In order for a BSN of length 8 to pass the test, a 0 is added as the first digit
        //TODO: tidy up this code, basically does the same thing twice
        if(fullNr.toString().length() == 8){
            digits.add(0, 0);
            while (fullNr > 0){
                digits.add(1, fullNr % 10);
                fullNr = fullNr / 10;
            }
        } else {
            while (fullNr > 0){
                digits.add(0, fullNr % 10);
                fullNr = fullNr / 10;
            }
        }
        return digits;
    }

    public int elevenTestSum(List<Integer> ints){
        int sum = 0;
        for (int i = 0; i < ints.size() - 1; i++) {
            sum += ints.get(i) * (ints.size() - i);
        }
        sum += (ints.get(8) * -1); //TODO: tidy? This can probably be done inside for loop
        return sum;
    }

}
