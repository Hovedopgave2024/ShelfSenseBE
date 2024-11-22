package com.example.shelfsensebe.Repository;

import com.example.shelfsensebe.DTO.ComponentSupplierDTO;
import com.example.shelfsensebe.Model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Integer> {

    List<Component> findByUser_Id(int userId);

    @Query("SELECT new com.example.shelfsensebe.DTO.ComponentSupplierDTO(c.id, c.supplierStock, c.manufacturer, c.manufacturerPart, c.supplierIncomingStock, c.supplierIncomingDate) FROM Component c WHERE c.supplier = :supplier")
    List<ComponentSupplierDTO> findBySupplier(@Param("supplier") String supplier);
}
