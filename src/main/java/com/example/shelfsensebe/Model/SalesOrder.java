package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class SalesOrder
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "price", nullable = false)
    @Min(0)
    private double price;

    @Column(name = "created_date", nullable = false)
    @PastOrPresent
    private Date createdDate;

    @JsonManagedReference("salesOrder-salesOrderProductList")
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<SalesOrderProduct> salesOrderProducts;

    @JsonBackReference("user-salesOrderList")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonProperty("userId")
    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }
}
