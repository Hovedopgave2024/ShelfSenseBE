package com.example.shelfsensebe.Repository;

import com.example.shelfsensebe.Model.SalesOrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderProductRepository extends JpaRepository<SalesOrderProduct, Integer> {
}
