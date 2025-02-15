package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @NotEmpty
    private String name;

    @Column(name = "type", nullable = false)
    @NotNull
    @NotEmpty
    private String type;

    @Column(name = "footprint", nullable = false)
    @NotNull
    @NotEmpty
    private String footprint;

    @Column(name = "manufacturer", nullable = false)
    @NotNull
    @NotEmpty
    private String manufacturer;

    @Column(name = "manufacturer_part", nullable = false)
    @NotNull
    @NotEmpty
    private String manufacturerPart;

    @Column(name = "price", nullable = false)
    @Min(0)
    private double price;

    @Column(name = "supplier", nullable = false)
    @NotNull
    @NotEmpty
    private String supplier;

    @Column(name = "stock", nullable = false)
    @Min(0)
    private int stock;

    @Column(name = "safety_stock", nullable = false)
    @Min(0)
    private int safetyStock;

    @Column(name = "safety_stock_rop", nullable = false)
    @Min(0)
    private int safetyStockRop;

    @Column(name = "supplier_stock")
    @Min(0)
    private Integer supplierStock;

    @Column(name = "supplier_safety_stock")
    @Min(0)
    private int supplierSafetyStock;

    @Column(name = "supplier_safety_stock_rop")
    @Min(0)
    private int supplierSafetyStockRop;

    @Column(name = "supplier_incoming_stock")
    @Min(0)
    private Integer supplierIncomingStock;

    @Column(name = "supplier_incoming_date")
    private Date supplierIncomingDate;

    @Column(name = "designator")
    private String designator;

    @Column(name = "supplier_part")
    private String supplierPart;

    @JsonIgnore
    @JsonManagedReference("component-productComponentList")
    @OneToMany(mappedBy = "component")
    private List<ProductComponent> productComponentList;

    @JsonBackReference("user-componentList")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonProperty("userId")
    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }

    @Transient
    @JsonProperty("stockStatus")
    private Integer stockStatus;

    @Transient
    @JsonProperty("supplierStockStatus")
    private Integer supplierStockStatus;
}
