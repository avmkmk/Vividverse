package com.vividverse.comment.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CommentResponse {
    private String id;
    private UUID postId;
    private UUID userId;
    private String parentCommentId;
    private String content;
    private LocalDateTime createdAt;
    // In a real app, you might add authorName here
}