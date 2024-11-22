package com.example.shelfsensebe.DTO;

import lombok.Data;

import java.sql.Date;

@Data
public class SupplierDTO {
    private int id;
    private int supplierStock;
    private int supplierSafetyStock;
    private int supplierIncomingStock;
    private Date supplierIncomingDate;

    public SupplierDTO(int id, int supplierStock, int supplierSafetyStock, int supplierIncomingStock, Date supplierIncomingDate) {
        this.id = id;
        this.supplierStock = supplierStock;
        this.supplierSafetyStock = supplierSafetyStock;
        this.supplierIncomingStock = supplierIncomingStock;
        this.supplierIncomingDate = supplierIncomingDate;
    }
}