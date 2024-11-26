package com.example.shelfsensebe.Repository;

import com.example.shelfsensebe.Model.ApiUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiUpdateRepository extends JpaRepository<ApiUpdate, Integer> {

    ApiUpdate findByUser_id(int userId);
}
