package com.example.shelfsensebe.utility;

import org.springframework.stereotype.Component;

@Component
public class TextSanitizer {

    public String sanitize(String input) {
        // Remove < and >, then clean up leading/trailing spaces
        return input.replaceAll("[<>]", "").replaceAll("^\\s+|\\s+$", "");
    }
}
