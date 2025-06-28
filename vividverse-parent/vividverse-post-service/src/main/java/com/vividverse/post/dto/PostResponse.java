package com.vividverse.post.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PostResponse {
    private UUID id;
    private String title;
    private String content;
    private UUID userId; // Author ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // In a real app, you might add authorName or commentCount here
}