package com.vividverse.user.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserProfileResponse {
    private UUID id;
    private String username;
    private String email;
    private String displayName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}