package com.example.shelfsensebe.Repository;

import com.example.shelfsensebe.Model.ProductComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductComponentRepository extends JpaRepository<ProductComponent, Integer>
{
    List<ProductComponent> findByProduct_id(int userId);
}
