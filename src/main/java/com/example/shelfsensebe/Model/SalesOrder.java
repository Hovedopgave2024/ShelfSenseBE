package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class SalesOrder
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @JsonBackReference("user-salesOrderList")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonBackReference("product-salesOrderList")
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
