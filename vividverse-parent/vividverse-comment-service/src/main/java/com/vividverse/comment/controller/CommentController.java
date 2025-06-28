package com.vividverse.comment.controller;

import com.vividverse.comment.dto.CommentCreateRequest;
import com.vividverse.comment.dto.CommentResponse;
import com.vividverse.comment.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@Valid @RequestBody CommentCreateRequest request) {
        CommentResponse newComment = commentService.addComment(request);
        return new ResponseEntity<>(newComment, HttpStatus.CREATED);
    }

    @GetMapping("/post/{postId}") // Endpoint to get comments for a specific post
    public ResponseEntity<List<CommentResponse>> getCommentsForPost(@PathVariable UUID postId) {
        List<CommentResponse> comments = commentService.getCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }
}

