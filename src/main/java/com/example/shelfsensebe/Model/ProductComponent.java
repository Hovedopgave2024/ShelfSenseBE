package com.example.shelfsensebe.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProductComponent
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "component_id")
    private Component component;

    @Column(name = "quantity", nullable = false)
    private int quantity;

}
