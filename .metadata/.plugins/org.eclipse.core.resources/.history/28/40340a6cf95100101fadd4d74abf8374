package com.vividverse.user.entity;

import jakarta.persistence.*;
import lombok.Data; // From Lombok for getters, setters, etc.
import java.time.LocalDateTime;
import java.util.UUID;

@Entity // Marks this class as a JPA entity
@Table(name = "users") // Maps to the 'users' table in the database
@Data // Lombok annotation to auto-generate getters, setters, equals, hashCode, toString
public class User {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.AUTO) // Auto-generates UUIDs
    private UUID id;

    @Column(unique = true, nullable = false) // Unique and cannot be null
    private String username;

    @Column(nullable = false)
    private String passwordHash; // Stores hashed password

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String displayName;

    @Column(updatable = false) // Value set on creation, not updated later
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist // Called before a new entity is saved (persisted)
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate // Called before an existing entity is updated
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}