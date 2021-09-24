package com.miw.service.authentication;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/*
@author Renée
Eleven check for Dutch social security numbers (BSN).
A valid BSN consists of 8 or 9 digits and passes a slightly altered version of the Modulo 11 test,
with the difference that the last value is multiplied by -1 instead of 1.
 */

public class ElevenCheckValidation implements ConstraintValidator<ElevenCheck, Integer> {

    final private int MIN_LENGTH = 8;
    final private int MAX_LENGTH = 9;

    @Override
    public void initialize(ElevenCheck constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer bsn, ConstraintValidatorContext constraintValidatorContext) {
        if (bsn.toString().length() < MIN_LENGTH || bsn.toString().length() > MAX_LENGTH){
            return false;
        }
        return elevenTestSum(makeDigits(bsn)) % 11 == 0;
    }

    //Turns an Integer into a list of its individual digits, always returns a list of 9 items
    private List<Integer> makeDigits(Integer fullNr){
        List<Integer> digits = new LinkedList<>();
        int temp = fullNr;

        while (temp > 0){
            digits.add(0, temp % 10);
            temp /= 10;
        }

        //In order for a BSN of length 8 to pass the test, a 0 is added as the first digit
        if(fullNr.toString().length() == MIN_LENGTH){
            digits.add(0, 0);
        }

        return digits;
    }

    private int elevenTestSum(List<Integer> ints){
        int sum = IntStream.range(0, ints.size() - 1).map(i -> ints.get(i) * (ints.size() - i)).sum();
        sum += (ints.get(8) * -1);
        return sum;
    }
}
