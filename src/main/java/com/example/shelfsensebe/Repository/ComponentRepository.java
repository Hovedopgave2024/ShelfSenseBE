package com.example.shelfsensebe.Repository;

import com.example.shelfsensebe.Model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Integer> {

    List<Component> findByUser_Id(int userId);

    List<Component> findBySupplierAndUser_Id(String supplier, int userId);


}
