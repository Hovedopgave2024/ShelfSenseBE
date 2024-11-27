package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Component
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "footprint", nullable = false)
    private String footprint;

    @Column(name = "manufacturer_part", nullable = false)
    private String manufacturerPart;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "supplier", nullable = false)
    private String supplier;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "safety_stock", nullable = false)
    private int safetyStock;

    @Column(name = "safety_stock_rop", nullable = false)
    private int safetyStockRop;

    @Column(name = "supplier_stock")
    private Integer supplierStock;

    @Column(name = "supplier_safety_stock")
    private Integer supplierSafetyStock;

    @Column(name = "supplier_safety_stock_rop")
    private Integer supplierSafetyStockRop;

    @Column(name = "supplier_incoming_stock")
    private Integer supplierIncomingStock;

    @Column(name = "supplier_incoming_date")
    private Date supplierIncomingDate;

    @Column(name = "designator")
    private String designator;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "supplier_part")
    private String supplierPart;

    @Column(name = "stock_status", nullable = false)
    private int stockStatus;

    @Column(name = "supplier_stock_status")
    private int supplierStockStatus;

    @JsonIgnore
    @JsonManagedReference("component-productComponentList")
    @OneToMany(mappedBy = "component")
    private List<ProductComponent> productComponentList;

    @JsonBackReference("user-componentList")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonProperty("userId")
    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }
}
