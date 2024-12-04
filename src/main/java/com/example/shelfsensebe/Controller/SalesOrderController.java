package com.example.shelfsensebe.Controller;
import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.SalesOrder;
import com.example.shelfsensebe.Repository.SalesOrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SalesOrderController {

    @Autowired
    SalesOrderRepository salesOrderRepository;


}
