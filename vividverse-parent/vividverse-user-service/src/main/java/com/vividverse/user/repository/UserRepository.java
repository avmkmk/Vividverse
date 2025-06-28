package com.vividverse.user.repository;

import com.vividverse.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

// JpaRepository provides methods for CRUD operations for the User entity
// It takes the Entity type (User) and the type of its primary key (UUID)
public interface UserRepository extends JpaRepository<User, UUID> {
    // Custom method to find a user by username
    Optional<User> findByUsername(String username);

    // Custom method to check if a username already exists
    boolean existsByUsername(String username);

    // Custom method to check if an email already exists
    boolean existsByEmail(String email);
}