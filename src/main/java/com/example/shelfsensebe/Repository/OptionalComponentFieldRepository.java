package com.example.shelfsensebe.Repository;

import com.example.shelfsensebe.Model.OptionalComponentField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionalComponentFieldRepository extends JpaRepository<OptionalComponentField, Integer> {

}