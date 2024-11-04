package com.example.rolepermission.repository;

import com.example.rolepermission.entity.AppFunction;
import com.example.rolepermission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppFunctionRepository extends JpaRepository<AppFunction, Long> {
    Optional<AppFunction> findByName(String name);

    @Query("SELECT f.permissions FROM AppFunction f WHERE f.id = ?1")
    List<Permission> findPermissionsByFunctionId(Long id);
}
