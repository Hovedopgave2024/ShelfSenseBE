package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ComponentController {

    @Autowired
    ComponentRepository componentRepository;

    @GetMapping("/components")
    public List<Component> getComponents() {
        return componentRepository.findAll();
    }

}
