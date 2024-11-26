package com.example.shelfsensebe.Repository;

import com.example.shelfsensebe.DTO.ComponentSupplierDTO;
import com.example.shelfsensebe.Model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Integer> {

    List<Component> findByUser_Id(int userId);

    @Query("SELECT new com.example.shelfsensebe.DTO.ComponentSupplierDTO(c.id, c.supplierStock, c.manufacturer, c.manufacturerPart, c.supplierIncomingStock, c.supplierIncomingDate) " +
            "FROM Component c WHERE c.supplier = :supplier AND c.user.id = :userId")
    List<ComponentSupplierDTO> findBySupplierAndUser(
            @Param("supplier") String supplier,
            @Param("userId") int userId);

    @Transactional
    @Modifying
    @Query("UPDATE Component c SET c.supplierStock = :stock, c.supplierIncomingStock = :incomingStock, c.supplierIncomingDate = :incomingDate WHERE c.id = :id")
    void updateComponentData(
            @Param("id") int id,
            @Param("stock") Integer stock,
            @Param("incomingStock") Integer incomingStock,
            @Param("incomingDate") Date incomingDate
    );
}
