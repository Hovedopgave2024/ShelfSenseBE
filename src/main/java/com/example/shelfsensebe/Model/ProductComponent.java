package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProductComponent
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @JsonBackReference("product-productComponentList")
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonProperty("productId")
    public Integer getProductId() {
        return product != null ? product.getId() : null;
    }

    @JsonBackReference("component-productComponentList")
    @ManyToOne
    @JoinColumn(name = "component_id")
    private Component component;

    @JsonProperty("componentId")
    public Integer getComponentId() {
        return component != null ? component.getId() : null;
    }

}
