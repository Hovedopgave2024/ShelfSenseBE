package com.example.shelfsensebe.DTO;

import lombok.Data;

import java.sql.Date;

@Data
public class VendorDTO {
    private int id;
    private int safetyStock;
    private int supplierIncomingStock;
    private Date supplierIncomingDate;

    public VendorDTO(int id, int safetyStock, int supplierIncomingStock, Date supplierIncomingDate) {
        this.id = id;
        this.safetyStock = safetyStock;
        this.supplierIncomingStock = supplierIncomingStock;
        this.supplierIncomingDate = supplierIncomingDate;
    }
}
