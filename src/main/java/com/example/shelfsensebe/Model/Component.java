package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @Column(name = "picture", nullable = true)
    private String picture;

    @Column(name = "designator", nullable = false)
    private String designator;

    @Column(name = "footprint", nullable = false)
    private String footprint;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @Column(name = "manufacturer_part", nullable = false)
    private String manufacturerPart;

    @Column(name = "supplier", nullable = false)
    private String supplier;

    @Column(name = "supplier_part", nullable = false)
    private String supplierPart;

    @JsonBackReference
    @OneToMany(mappedBy = "component")
    private List<ProductComponent> productComponentList;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
