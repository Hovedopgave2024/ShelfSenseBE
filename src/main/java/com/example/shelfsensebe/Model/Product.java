package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.*;
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

    @JsonManagedReference("product-productComponentList")
    @OneToMany(mappedBy = "product")
    private List<ProductComponent> productComponentList;

    @JsonManagedReference("product-salesOrderList")
    @OneToMany(mappedBy = "product")
    private List<SalesOrder> salesOrderList;

    @JsonBackReference("user-productList")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonProperty("userId")
    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }
}
