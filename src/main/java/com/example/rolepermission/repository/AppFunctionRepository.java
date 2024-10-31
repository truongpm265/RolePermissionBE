package com.example.rolepermission.repository;

import com.example.rolepermission.entity.AppFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppFunctionRepository extends JpaRepository<AppFunction, Long> {
    Optional<AppFunction> findByName(String name);
}
