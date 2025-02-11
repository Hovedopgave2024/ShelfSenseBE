package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SalesOrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "quantity", nullable = false)
    @Min(1)
    private int quantity;

    @Column(name = "product_id", nullable = false)
    @Min(1)
    private int productId;

    @JsonIgnore
    @JsonBackReference("salesOrder-salesOrderProductList")
    @ManyToOne
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    @JsonProperty("salesOrderid")
    public Integer getSalesOrderId() {
        return salesOrder != null ? salesOrder.getId() : null;
    }
}