package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.UpdateUserDTO;
import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.shelfsensebe.utility.TextSanitizer;
import com.example.shelfsensebe.utility.PasswordValidator;

import java.util.Optional;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TextSanitizer textSanitizer;

    @Autowired
    private PasswordValidator passwordValidator;

    public User createUser(User user) {
        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (
                !passwordValidator.isValidPassword(user.getPassword())
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setName(textSanitizer.sanitize(user.getName()));
        return userRepository.save(user);
    }

    public UserDTO updateUser(UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(updateUserDTO.getId());
        Optional<User> findUserByName = userRepository.findByName(updateUserDTO.getName());

        if (findUserByName.isPresent() && findUserByName.get().getId() != user.getId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (
                !passwordValidator.isValidPassword(updateUserDTO.getNewPassword()) ||
                        !passwordEncoder.matches(updateUserDTO.getOldPassword(), user.getPassword()) ||
                        passwordEncoder.matches(updateUserDTO.getNewPassword(), user.getPassword())

        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        user.setName(textSanitizer.sanitize(updateUserDTO.getName()));
        user.setPassword(passwordEncoder.encode(updateUserDTO.getNewPassword()));
        userRepository.save(user);

        return new UserDTO(user.getId(), updateUserDTO.getName());
    }
}