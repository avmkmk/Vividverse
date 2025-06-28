package com.vividverse.comment.entity;

import lombok.Data;
import org.springframework.data.annotation.Id; // MongoDB specific ID
import org.springframework.data.mongodb.core.mapping.Document; // Marks as MongoDB document
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "comments") // Maps to 'comments' collection in MongoDB
@Data
public class Comment {

    @Id // Marks this field as the primary key for MongoDB
    private String id; // MongoDB IDs are typically Strings

    private UUID postId; // Logical FK to Post Service
    private UUID userId; // Logical FK to User Service
    private String parentCommentId; // Optional, for replies

    private String content;

    private LocalDateTime createdAt;

    public Comment() {
        this.id = UUID.randomUUID().toString(); // Generate UUID string for MongoDB ID
        this.createdAt = LocalDateTime.now();
    }
}
