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
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @JsonProperty("productId")
    public Integer getProductId() {
        return product != null ? product.getId() : null;
    }

    @JsonBackReference("component-productComponentList")
    @ManyToOne
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;

    @Transient
    private Integer componentId;

    @JsonProperty("componentId")
    public Integer getComponentId() {
        return (componentId != null) ? componentId : (component != null ? component.getId() : null);
    }

}
