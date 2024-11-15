package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Model.SalesOrder;
import com.example.shelfsensebe.Repository.ProductRepository;
import com.example.shelfsensebe.Repository.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SalesOrderController {

    @Autowired
    SalesOrderRepository salesOrderRepository;

    @GetMapping("/salesorders")
    public List<SalesOrder> getSalesOrder() {
        return salesOrderRepository.findAll();
    }
}
