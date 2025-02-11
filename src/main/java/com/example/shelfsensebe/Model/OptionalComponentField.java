package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OptionalComponentField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    @NotNull
    @NotEmpty
    private String name;

    @Column(name = "value", nullable = false)
    @NotNull
    @NotEmpty
    private String value;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;
}
