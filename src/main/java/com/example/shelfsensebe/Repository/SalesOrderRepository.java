package com.example.shelfsensebe.Repository;

import com.example.shelfsensebe.Model.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Integer> {


}
