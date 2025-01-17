package com.example.shelfsensebe.DTO.MouserApiDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MouserRequestDTO {
    int userId;
    String apiKey;

    public MouserRequestDTO(int userId, String apiKey) {
        this.userId = userId;
        this.apiKey = apiKey;
    }
}
