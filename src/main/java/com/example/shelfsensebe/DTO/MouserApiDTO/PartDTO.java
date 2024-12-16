package com.example.shelfsensebe.DTO.MouserApiDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PartDTO {
    private String Manufacturer;
    private String ManufacturerPartNumber;
    private int AvailabilityInStock;
    private List<AvailabilityOnOrderDTO> AvailabilityOnOrder;

    public PartDTO(String Manufacturer, String ManufacturerPartNumber, int AvailabilityInStock, List<AvailabilityOnOrderDTO> AvailabilityOnOrder) {
        this.Manufacturer = Manufacturer;
        this.ManufacturerPartNumber = ManufacturerPartNumber;
        this.AvailabilityInStock = AvailabilityInStock;
        this.AvailabilityOnOrder = AvailabilityOnOrder;
    }
}
