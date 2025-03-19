package com.example.shelfsensebe.utility;

import org.springframework.stereotype.Component;

@Component
public class TextSanitizer {

    public String sanitize(String input) {
        if (input == null) {
            return null; // or return a default value
        }
        return input.replaceAll("<", "")
                .replaceAll(">", "")
                .replaceAll("&", "")
                .replaceAll("\"", "")
                .replaceAll("'", "")
                .replaceAll("\\\\", "");
    }
}
