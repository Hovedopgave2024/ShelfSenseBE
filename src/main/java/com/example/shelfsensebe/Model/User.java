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

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Lob
    @Column(name = "picture", nullable = true)
    private byte[] picture;


    @JsonManagedReference("user-componentList")
    @OneToMany(mappedBy = "user")
    private List<Component> componentList;

    @JsonManagedReference("user-productList")
    @OneToMany(mappedBy = "user")
    private List<Product> productList;

    @JsonManagedReference("user-salesOrderList")
    @OneToMany(mappedBy = "user")
    private List<SalesOrder> salesOrderList;


}
