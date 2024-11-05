package com.example.shelfsensebe.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String password;

    private String picture;

    @OneToMany(mappedBy = "user")
    private List<Component> componentList;

    @OneToMany(mappedBy = "user")
    private List<Product> productList;

    @OneToMany(mappedBy = "user")
    private List<SalesOrder> salesOrderList;



}
