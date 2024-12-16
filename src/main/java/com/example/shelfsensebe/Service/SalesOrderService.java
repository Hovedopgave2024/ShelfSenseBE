package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.SalesOrder;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ProductRepository;
import com.example.shelfsensebe.Repository.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;

@Service
public class SalesOrderService {

    @Autowired
    SalesOrderRepository salesOrderRepository;
    @Autowired
    private ProductRepository productRepository;

    public SalesOrder createSalesOrder (SalesOrder salesOrder, UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());

        salesOrder.setUser(user);
        salesOrder.setCreatedDate(salesOrder.getCreatedDate());
        salesOrder.setPrice(salesOrder.getPrice());
        salesOrder.setQuantity(salesOrder.getQuantity());
        salesOrder.setProductId(salesOrder.getProductId());

        productRepository.findById(salesOrder.getProductId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found")
        );

        return salesOrderRepository.save(salesOrder);
    }

    public SalesOrder updateSalesOrder(SalesOrder salesOrder, UserDTO userDTO) {
        SalesOrder existingSalesOrder = salesOrderRepository.findById(salesOrder.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sales order not found")
        );

        User user = new User();
        user.setId(userDTO.getId());

        existingSalesOrder.setUser(user);
        existingSalesOrder.setQuantity(salesOrder.getQuantity());
        existingSalesOrder.setPrice(salesOrder.getPrice());
        existingSalesOrder.setProductId(salesOrder.getProductId());
        existingSalesOrder.setCreatedDate(salesOrder.getCreatedDate());

        productRepository.findById(salesOrder.getProductId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found")
        );

        return salesOrderRepository.save(existingSalesOrder);
    }

    public void deleteSalesOrder(int id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sales order not found")
        );
        salesOrderRepository.delete(salesOrder);
    }
}
