package com.example.shelfsensebe.utility;

import org.springframework.stereotype.Component;

@Component
public class IntValidator {
    public Integer validateint(Integer input, Integer min, Integer max) {
        if (input == null) {
            return null;
        }
        if (input < min || input > max) {
            throw new IllegalArgumentException("Input must be between " + min + " and " + max);
        }
        return input;
    }
}
