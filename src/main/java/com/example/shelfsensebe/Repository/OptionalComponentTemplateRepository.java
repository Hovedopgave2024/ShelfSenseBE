package com.example.shelfsensebe.Repository;

import com.example.shelfsensebe.Model.OptionalComponentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionalComponentTemplateRepository extends JpaRepository<OptionalComponentTemplate, Integer>
{

}
