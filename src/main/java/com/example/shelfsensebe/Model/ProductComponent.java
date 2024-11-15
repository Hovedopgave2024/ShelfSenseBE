package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProductComponent
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonBackReference("product-productComponentList")
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonBackReference("component-productComponentList")
    @ManyToOne
    @JoinColumn(name = "component_id")
    private Component component;

    @Column(name = "quantity", nullable = false)
    private int quantity;

}
