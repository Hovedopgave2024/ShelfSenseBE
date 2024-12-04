package com.example.shelfsensebe.utility;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false; // Check for minimum length
        }

        boolean hasLowerCase = false;
        boolean hasUpperCase = false;
        boolean hasSpecialCharacter = false;

        for (char ch : password.toCharArray()) {
            if (Character.isLowerCase(ch)) {
                hasLowerCase = true;
            } else if (Character.isUpperCase(ch)) {
                hasUpperCase = true;
            } else if (!Character.isLetterOrDigit(ch)) {
                hasSpecialCharacter = true;
            }

            // If all conditions are met, no need to keep checking
            if (hasLowerCase && hasUpperCase && hasSpecialCharacter) {
                return true;
            }
        }

        // If any of the conditions is not met, return false
        return false;
    }
}
