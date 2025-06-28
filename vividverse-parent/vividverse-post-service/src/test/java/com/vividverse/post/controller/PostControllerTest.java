package com.vividverse.post.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vividverse.post.dto.PostCreateRequest;
import com.vividverse.post.dto.PostResponse;
import com.vividverse.post.dto.PostUpdateRequest;
import com.vividverse.post.service.PostService;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private PostCreateRequest createRequest;
    private PostUpdateRequest updateRequest;
    private PostResponse postResponse;
    private UUID testPostId;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testPostId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        
        createRequest = new PostCreateRequest();
        createRequest.setTitle("New Post");
        createRequest.setContent("This is a new post content");
        createRequest.setUserId(testUserId);

        updateRequest = new PostUpdateRequest();
        updateRequest.setTitle("Updated Post");
        updateRequest.setContent("This is an updated post content");

        postResponse = new PostResponse();
        postResponse.setId(testPostId);
        postResponse.setTitle("Test Post");
        postResponse.setContent("This is a test post content");
        postResponse.setUserId(testUserId);
        postResponse.setCreatedAt(LocalDateTime.now());
        postResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreatePost_Success() {
        // Arrange
        when(postService.createPost(any(PostCreateRequest.class))).thenReturn(postResponse);

        // Act
        ResponseEntity<PostResponse> response = postController.createPost(createRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createRequest.getTitle(), response.getBody().getTitle());
        assertEquals(createRequest.getContent(), response.getBody().getContent());
        assertEquals(createRequest.getUserId(), response.getBody().getUserId());
    }

    @Test
    void testCreatePost_WithNullValues() {
        // Arrange
        PostCreateRequest nullRequest = new PostCreateRequest();
        // Don't set any values, leaving them null

        when(postService.createPost(any(PostCreateRequest.class))).thenReturn(postResponse);

        // Act
        ResponseEntity<PostResponse> response = postController.createPost(nullRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetPostById_Success() {
        // Arrange
        when(postService.getPostById(testPostId)).thenReturn(Optional.of(postResponse));

        // Act
        ResponseEntity<PostResponse> response = postController.getPostById(testPostId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testPostId, response.getBody().getId());
        assertEquals(postResponse.getTitle(), response.getBody().getTitle());
    }

    @Test
    void testGetPostById_NotFound() {
        // Arrange
        UUID nonExistentPostId = UUID.randomUUID();
        when(postService.getPostById(nonExistentPostId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<PostResponse> response = postController.getPostById(nonExistentPostId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetPostById_WithNullId() {
        // Act
        ResponseEntity<PostResponse> response = postController.getPostById(null);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAllRecentPosts_Success() {
        // Arrange
        Page<PostResponse> postPage = new PageImpl<>(java.util.List.of(postResponse));
        when(postService.getAllRecentPosts(anyInt(), anyInt())).thenReturn(postPage);

        // Act
        ResponseEntity<Page<PostResponse>> response = postController.getAllRecentPosts(0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(testPostId, response.getBody().getContent().get(0).getId());
    }

    @Test
    void testGetAllRecentPosts_EmptyPage() {
        // Arrange
        Page<PostResponse> emptyPage = new PageImpl<>(java.util.List.of());
        when(postService.getAllRecentPosts(anyInt(), anyInt())).thenReturn(emptyPage);

        // Act
        ResponseEntity<Page<PostResponse>> response = postController.getAllRecentPosts(0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getTotalElements());
        assertEquals(0, response.getBody().getContent().size());
    }

    @Test
    void testGetAllRecentPosts_WithCustomPagination() {
        // Arrange
        Page<PostResponse> postPage = new PageImpl<>(java.util.List.of(postResponse));
        when(postService.getAllRecentPosts(2, 5)).thenReturn(postPage);

        // Act
        ResponseEntity<Page<PostResponse>> response = postController.getAllRecentPosts(2, 5);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(postService).getAllRecentPosts(2, 5);
    }

    @Test
    void testGetAllRecentPosts_WithDefaultValues() {
        // Arrange
        Page<PostResponse> postPage = new PageImpl<>(java.util.List.of(postResponse));
        when(postService.getAllRecentPosts(0, 10)).thenReturn(postPage);

        // Act
        ResponseEntity<Page<PostResponse>> response = postController.getAllRecentPosts(null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(postService).getAllRecentPosts(0, 10);
    }

    @Test
    void testUpdatePost_Success() {
        // Arrange
        when(postService.updatePost(testPostId, updateRequest)).thenReturn(Optional.of(postResponse));

        // Act
        ResponseEntity<PostResponse> response = postController.updatePost(testPostId, updateRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testPostId, response.getBody().getId());
    }

    @Test
    void testUpdatePost_NotFound() {
        // Arrange
        UUID nonExistentPostId = UUID.randomUUID();
        when(postService.updatePost(nonExistentPostId, updateRequest)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<PostResponse> response = postController.updatePost(nonExistentPostId, updateRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdatePost_WithNullValues() {
        // Arrange
        PostUpdateRequest nullRequest = new PostUpdateRequest();
        // Don't set any values, leaving them null

        when(postService.updatePost(testPostId, nullRequest)).thenReturn(Optional.of(postResponse));

        // Act
        ResponseEntity<PostResponse> response = postController.updatePost(testPostId, nullRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testDeletePost_Success() {
        // Arrange
        when(postService.deletePost(testPostId)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = postController.deletePost(testPostId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeletePost_NotFound() {
        // Arrange
        UUID nonExistentPostId = UUID.randomUUID();
        when(postService.deletePost(nonExistentPostId)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = postController.deletePost(nonExistentPostId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeletePost_WithNullId() {
        // Act
        ResponseEntity<Void> response = postController.deletePost(null);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreatePost_ServiceThrowsException() {
        // Arrange
        when(postService.createPost(any(PostCreateRequest.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            postController.createPost(createRequest);
        });
    }

    @Test
    void testGetAllRecentPosts_ServiceThrowsException() {
        // Arrange
        when(postService.getAllRecentPosts(anyInt(), anyInt()))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            postController.getAllRecentPosts(0, 10);
        });
    }
} 