package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Entity
public class Supplier
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // supplier from component table
    @Column(name = "name", nullable = false)
    @NotNull
    @NotEmpty
    private String name;

    @OneToOne
    @JoinColumn(name = "component_id", nullable = false, unique = true)
    @JsonBackReference("component-supplier")
    private Component component;

    @Column(name = "manufacturer", nullable = false)
    @NotNull
    @NotEmpty
    private String manufacturer;

    @Column(name = "manufacturer_part", nullable = false)
    @NotNull
    @NotEmpty
    private String manufacturerPart;

    @Column(name = "stock")
    @Min(0)
    private Integer stock;

    @Column(name = "incoming_stock")
    @Min(0)
    private Integer incomingStock;

    @Column(name = "incoming_date")
    private Date incomingDate;

    @Column(name = "safety_stock")
    @Min(0)
    private int safetyStock;

    @Column(name = "safety_stock_rop")
    @Min(0)
    private int safetyStockRop;

    @Column(name = "supplier_part")
    private String supplierPart;

    @Transient
    @JsonProperty("stockStatus")
    private Integer stockStatus;
}
