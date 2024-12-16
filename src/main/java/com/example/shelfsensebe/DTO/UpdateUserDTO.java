package com.example.shelfsensebe.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDTO {
    private int id;
    private String name;
    private String oldPassword;
    private String newPassword;

    public UpdateUserDTO(int id, String name, String oldPassword, String newPassword) {
        this.id = id;
        this.name = name;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
