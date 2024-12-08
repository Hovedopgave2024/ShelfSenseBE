package com.example.shelfsensebe.DTO.MouserApiDTO;

import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Getter
@Setter
public class AvailabilityOnOrderDTO {
    private int Quantity;
    private Date Date;

    public AvailabilityOnOrderDTO(int Quantity, Date Date) {
        this.Quantity = Quantity;
        this.Date = Date;
    }
}

