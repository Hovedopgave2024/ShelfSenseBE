package com.example.shelfsensebe.Repository;

import com.example.shelfsensebe.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByUser_Id(int userId);

    boolean existsByName(String name);

    boolean existsByNameAndUser_Id(String name, int userId);

}
