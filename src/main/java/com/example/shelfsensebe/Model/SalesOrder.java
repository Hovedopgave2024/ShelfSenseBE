package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;


@Entity
@Data
public class SalesOrder
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @JsonBackReference("user-salesOrderList")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonProperty("userId")
    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }

    @JsonBackReference("product-salesOrderList")
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonProperty("productId")
    public Integer getProductId() {
        return product != null ? product.getId() : null;
    }
}
