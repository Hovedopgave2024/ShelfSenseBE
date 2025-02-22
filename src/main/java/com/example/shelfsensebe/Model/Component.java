package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
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

    @Column(name = "price", nullable = false)
    @Min(0)
    private double price;

    @Column(name = "stock", nullable = false)
    @Min(0)
    private int stock;

    @Column(name = "safety_stock", nullable = false)
    @Min(0)
    private int safetyStock;

    @Column(name = "safety_stock_rop", nullable = false)
    @Min(0)
    private int safetyStockRop;

    @Transient
    @JsonProperty("stockStatus")
    private Integer stockStatus;

    // Going into optional component fields
    @Column(name = "type", nullable = false)
    @NotNull
    @NotEmpty
    private String type;

    // Going into optional component fields
    @Column(name = "footprint", nullable = false)
    @NotNull
    @NotEmpty
    private String footprint;

    // Going into optional component fields
    @Column(name = "designator")
    private String designator;

    @OneToOne(mappedBy = "component", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("component-supplier")
    @Valid
    private Supplier supplier;

    @JsonManagedReference("component-optionalComponentField")
    @OneToMany(mappedBy = "component", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<OptionalComponentField> optionalComponentFields;

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
}
