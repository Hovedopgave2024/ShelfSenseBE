package com.example.shelfsensebe.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Data
@Entity
public class ApiUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date lastUpdated;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonProperty("userId")
    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }
}