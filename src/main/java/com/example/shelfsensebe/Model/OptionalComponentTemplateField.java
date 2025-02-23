package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OptionalComponentTemplateField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    @NotNull
    @NotEmpty
    private String name;

    @JsonBackReference("optionalComponentTemplate-optionalComponentTemplateField")
    @ManyToOne
    @JoinColumn(name = "optionalComponentTemplate_id", nullable = false)
    private OptionalComponentTemplate optionalComponentTemplate;
}
