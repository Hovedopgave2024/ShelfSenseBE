package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ComponentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComponentService
{
    @Autowired
    private ComponentRepository componentRepository;

    public Component createComponent(Component component, UserDTO userDTO) {
        // Map UserDTO to User
        User user = new User();
        user.setId(userDTO.getId());
        component.setUser(user);
        // Save and return the component
        return componentRepository.save(component);
    }
}
