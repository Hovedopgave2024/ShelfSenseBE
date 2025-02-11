package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    @NotNull
    @NotEmpty
    private String name;

    @Column(name = "manufacturer", nullable = false)
    @NotNull
    @NotEmpty
    private String manufacturer;

    @Column(name = "manufacturer_part", nullable = false)
    @NotNull
    @NotEmpty
    private String manufacturerPart;

    @Column(name = "supplier_stock")
    @Min(0)
    private Integer supplierStock;

    @Column(name = "supplier_incoming_stock")
    @Min(0)
    private Integer supplierIncomingStock;

    @Column(name = "supplier_incoming_date")
    private Date supplierIncomingDate;

    @Column(name = "supplier_part")
    private String supplierPart;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;
}
