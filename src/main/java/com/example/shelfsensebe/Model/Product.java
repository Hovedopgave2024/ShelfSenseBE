package com.example.shelfsensebe.Model;

import jakarta.persistence.*;
import lombok.Data;


import java.util.List;

@Entity
@Data
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "picture", nullable = true)
    private String picture;

    @OneToMany(mappedBy = "product")
    private List<ProductComponent> productComponentList;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "product")
    private List<SalesOrder> salesOrderList;







}
