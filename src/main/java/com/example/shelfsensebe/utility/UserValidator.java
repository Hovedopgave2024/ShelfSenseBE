package com.example.shelfsensebe.utility;

import com.example.shelfsensebe.DTO.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class UserValidator {
    public void validateSessionUser(HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User is not authorized");
        }
    }
}
