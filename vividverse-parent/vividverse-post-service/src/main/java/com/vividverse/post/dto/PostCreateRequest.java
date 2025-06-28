package com.vividverse.post.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull; // Added for UUID validation
import java.util.UUID;

@Data
public class PostCreateRequest {
    @NotBlank(message = "Title cannot be empty")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters")
    private String title;

    @NotBlank(message = "Content cannot be empty")
    @Size(min = 20, message = "Content must be at least 20 characters")
    private String content;

    @NotNull(message = "User ID cannot be null") // Author of the post
    private UUID userId;
}