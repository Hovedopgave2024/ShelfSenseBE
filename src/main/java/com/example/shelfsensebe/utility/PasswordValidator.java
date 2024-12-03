package com.example.shelfsensebe.utility;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {
    public boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }
}
