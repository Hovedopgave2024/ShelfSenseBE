package com.example.shelfsensebe.Repository;

import com.example.shelfsensebe.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findById(int id);
    Optional<User>findByName(String name);
}
