package com.vividverse.post.controller;

import com.vividverse.post.dto.PostCreateRequest;
import com.vividverse.post.dto.PostResponse;
import com.vividverse.post.dto.PostUpdateRequest;
import com.vividverse.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostCreateRequest request) {
        PostResponse newPost = postService.createPost(request);
        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable UUID postId) {
        return postService.getPostById(postId)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping // Handles /posts and /posts?page=X&size=Y
    public ResponseEntity<Page<PostResponse>> getAllRecentPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostResponse> posts = postService.getAllRecentPosts(page, size);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable UUID postId,
                                                   @Valid @RequestBody PostUpdateRequest request) {
        return postService.updatePost(postId, request)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID postId) {
        if (postService.deletePost(postId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
