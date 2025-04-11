package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class OptionalComponentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    @NotNull
    @NotEmpty
    private String name;

    @JsonManagedReference("optionalComponentTemplate-optionalComponentTemplateField")
    @OneToMany(mappedBy = "optionalComponentTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<OptionalComponentTemplateField> optionalComponentTemplateFields;

    @JsonBackReference("user-optionalComponentTemplate")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
