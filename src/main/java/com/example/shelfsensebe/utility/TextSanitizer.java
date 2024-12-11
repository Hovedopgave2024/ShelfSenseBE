package com.example.shelfsensebe.utility;

import org.springframework.stereotype.Component;

@Component
public class TextSanitizer {

    public String sanitize(String input) {
            return input.replaceAll("<", "")
                .replaceAll(">", "")
                .replaceAll("&", "")
                .replaceAll("\"", "")
                .replaceAll("'", "")
                .replaceAll("\\\\", "");
    }
}
