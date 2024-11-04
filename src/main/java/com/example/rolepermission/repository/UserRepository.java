package com.example.rolepermission.repository;

import com.example.rolepermission.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE (:name IS NULL OR u.username LIKE %:name%) " +
            "AND (:email IS NULL OR u.email LIKE %:email%) " +
            "AND (:role IS NULL OR r.name LIKE %:role%)")
    List<User> searchUsers(@Param("name") String name,
                           @Param("email") String email,
                           @Param("role") String role);
}
