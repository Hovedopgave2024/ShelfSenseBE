package com.example.shelfsensebe.DTO;

import lombok.Data;

import java.sql.Date;

@Data
public class ComponentSupplierDTO {
    private int id;
    private int supplierStock;
    private String manufacturer;
    private String manufacturerPart;
    private int supplierIncomingStock;
    private Date supplierIncomingDate;

    public ComponentSupplierDTO(int id, int supplierStock, String manufacturer, String manufacturerPart, int supplierIncomingStock, Date supplierIncomingDate) {
        this.id = id;
        this.supplierStock = supplierStock;
        this.manufacturer = manufacturer;
        this.manufacturerPart = manufacturerPart;
        this.supplierIncomingStock = supplierIncomingStock;
        this.supplierIncomingDate = supplierIncomingDate;
    }
}