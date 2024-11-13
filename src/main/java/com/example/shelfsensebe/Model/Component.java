package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Component
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "type", nullable = false)
    private String type;

    @Lob
    @Column(name = "picture", nullable = true)
    private byte[] picture;

    @Column(name = "designator", nullable = false)
    private String designator;

    @Column(name = "footprint", nullable = false)
    private String footprint;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "safety_stock", nullable = false)
    private int safetyStock;

    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @Column(name = "manufacturer_part", nullable = false)
    private String manufacturerPart;

    @Column(name = "supplier", nullable = false)
    private String supplier;

    @Column(name = "supplier_part", nullable = false)
    private String supplierPart;

    @JsonManagedReference("component-productComponentList")
    @OneToMany(mappedBy = "component")
    private List<ProductComponent> productComponentList;

    @JsonBackReference("user-componentList")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
