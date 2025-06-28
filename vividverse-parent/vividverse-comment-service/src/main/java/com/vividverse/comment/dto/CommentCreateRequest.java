package com.vividverse.comment.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CommentCreateRequest {
    @NotNull(message = "Post ID cannot be null")
    private UUID postId;

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    private String parentCommentId; // Null for top-level comments, UUID string for replies

    @NotBlank(message = "Comment content cannot be empty")
    private String content;
}
