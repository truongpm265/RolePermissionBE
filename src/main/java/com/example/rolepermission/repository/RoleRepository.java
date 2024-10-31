package com.example.rolepermission.repository;

import com.example.rolepermission.entity.Role;
import com.example.rolepermission.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,String> {
    Optional<Role> findByName(String role);

    @Query("SELECT r FROM Role r JOIN FETCH r.functions f JOIN FETCH f.permissions WHERE r.name = :roleName")
    Role findByNameWithFunctionsAndPermissions(@Param("roleName") String roleName);
}
