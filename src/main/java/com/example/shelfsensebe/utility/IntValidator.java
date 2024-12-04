package com.example.shelfsensebe.utility;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class IntValidator {
    public Integer validateint(Integer input, Integer min, Integer max, boolean nullable) throws BadRequestException {
        if (input == null && nullable) {
            return null;
        }
        if (input == null) {
            throw new BadRequestException();
        }
        if (input < min || input > max) {
            throw new BadRequestException();
        }
        return input;
    }
    public double validateDouble(double input, Double min, Double max, boolean nullable) throws BadRequestException {
        if ((min != null && input < min) || (max != null && input > max)) {
            throw new BadRequestException();
        }
        return input;
    }
}
