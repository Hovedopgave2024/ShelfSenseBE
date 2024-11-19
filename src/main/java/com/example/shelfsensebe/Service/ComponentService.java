package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ComponentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComponentService {

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private UserService userService;

    public Component createComponent(Component component, HttpSession session) {
        // Validate and retrieve the logged-in user
        User user = userService.getCurrentUser(session);

        // Associate the component with the user
        component.setUser(user);

        // Save the component to the database
        return componentRepository.save(component);
    }
}
