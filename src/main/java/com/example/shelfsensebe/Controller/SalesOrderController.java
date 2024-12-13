package com.example.shelfsensebe.Controller;
import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.SalesOrder;
import com.example.shelfsensebe.Service.SalesOrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SalesOrderController {

    @Autowired
    SalesOrderService salesOrderService;

    @PostMapping("/salesOrders")
    public ResponseEntity<SalesOrder> createSalesOrder(@Valid @RequestBody SalesOrder salesOrder, HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SalesOrder savedSalesOrder = salesOrderService.createSalesOrder(salesOrder, userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSalesOrder);
    }

    @PutMapping("/salesOrders")
    public ResponseEntity<SalesOrder> updateSalesOrder(@Valid @RequestBody SalesOrder updatedSalesOrder, HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SalesOrder savedSalesOrder = salesOrderService.updateSalesOrder(updatedSalesOrder, userDTO);
        return ResponseEntity.ok(savedSalesOrder);
    }

    @DeleteMapping("/salesOrders")
    public ResponseEntity<Void> deleteSalesOrder(@Valid @RequestBody int id, HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        salesOrderService.deleteSalesOrder(id);
        return ResponseEntity.noContent().build();
    }
}
