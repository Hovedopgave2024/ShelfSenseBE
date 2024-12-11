package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProductComponent
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "quantity", nullable = false)
    @Min(1)
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

    @Transient
    private Integer componentId; // To capture value from frontend

    @JsonProperty("componentId")
    public Integer getComponentId() {
        // Use componentId from the frontend if set, otherwise fallback to component.getId()
        return (componentId != null) ? componentId : (component != null ? component.getId() : null);
    }

}
