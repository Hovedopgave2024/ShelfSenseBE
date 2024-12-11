package com.example.shelfsensebe.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @JsonManagedReference("user-componentList")
    @OneToMany(mappedBy = "user")
    @Valid
    private List<Component> componentList;

    @JsonManagedReference("user-productList")
    @OneToMany(mappedBy = "user")
    @Valid
    private List<Product> productList;

    @JsonManagedReference("user-salesOrderList")
    @OneToMany(mappedBy = "user")
    @Valid
    private List<SalesOrder> salesOrderList;

    @JsonManagedReference("user-apiUpdate")
    @OneToOne(mappedBy = "user")
    @Valid
    private ApiUpdate apiUpdate;
}
