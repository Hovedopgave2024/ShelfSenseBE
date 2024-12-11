package com.example.shelfsensebe.utility;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class NumberValidator {
    public Integer validateInt(int input) throws BadRequestException {
        if (input < 0) {
            throw new BadRequestException();
        }
        return input;
    }
    public double validateDouble(double input, Double min, Double max) throws BadRequestException {
        if ((min != null && input < min) || (max != null && input > max)) {
            throw new BadRequestException();
        }
        return input;
    }
}
