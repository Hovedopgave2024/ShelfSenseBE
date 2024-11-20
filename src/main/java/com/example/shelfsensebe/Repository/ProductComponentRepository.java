package com.example.shelfsensebe.Repository;

import com.example.shelfsensebe.Model.ProductComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductComponentRepository extends JpaRepository<ProductComponent, Integer>
{
}
