package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Component> componentList;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Product> productList;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<SalesOrder> salesOrderList;



}
