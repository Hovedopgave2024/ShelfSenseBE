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

    public Component createComponent(Component component, User user) {
        // Associate the component with the user
        component.setUser(user);
        // Save the component to the database
        return componentRepository.save(component);
    }
}
