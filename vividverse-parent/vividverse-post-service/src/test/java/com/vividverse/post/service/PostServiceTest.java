package com.vividverse.post.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.vividverse.post.dto.PostCreateRequest;
import com.vividverse.post.dto.PostResponse;
import com.vividverse.post.dto.PostUpdateRequest;
import com.vividverse.post.entity.Post;
import com.vividverse.post.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private PostCreateRequest createRequest;
    private PostUpdateRequest updateRequest;
    private UUID testPostId;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testPostId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        
        testPost = new Post();
        testPost.setId(testPostId);
        testPost.setTitle("Test Post");
        testPost.setContent("This is a test post content");
        testPost.setUserId(testUserId);
        testPost.setCreatedAt(LocalDateTime.now());
        testPost.setUpdatedAt(LocalDateTime.now());

        createRequest = new PostCreateRequest();
        createRequest.setTitle("New Post");
        createRequest.setContent("This is a new post content");
        createRequest.setUserId(testUserId);

        updateRequest = new PostUpdateRequest();
        updateRequest.setTitle("Updated Post");
        updateRequest.setContent("This is an updated post content");
    }

    @Test
    void testCreatePost_Success() {
        // Arrange
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        PostResponse result = postService.createPost(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(createRequest.getTitle(), result.getTitle());
        assertEquals(createRequest.getContent(), result.getContent());
        assertEquals(createRequest.getUserId(), result.getUserId());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testCreatePost_WithNullValues() {
        // Arrange
        PostCreateRequest nullRequest = new PostCreateRequest();
        // Don't set any values, leaving them null

        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        PostResponse result = postService.createPost(nullRequest);

        // Assert
        assertNotNull(result);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testGetPostById_Success() {
        // Arrange
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));

        // Act
        Optional<PostResponse> result = postService.getPostById(testPostId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testPostId, result.get().getId());
        assertEquals(testPost.getTitle(), result.get().getTitle());
        assertEquals(testPost.getContent(), result.get().getContent());
        verify(postRepository).findById(testPostId);
    }

    @Test
    void testGetPostById_NotFound() {
        // Arrange
        UUID nonExistentPostId = UUID.randomUUID();
        when(postRepository.findById(nonExistentPostId)).thenReturn(Optional.empty());

        // Act
        Optional<PostResponse> result = postService.getPostById(nonExistentPostId);

        // Assert
        assertFalse(result.isPresent());
        verify(postRepository).findById(nonExistentPostId);
    }

    @Test
    void testGetAllRecentPosts_Success() {
        // Arrange
        Page<Post> postPage = new PageImpl<>(java.util.List.of(testPost));
        when(postRepository.findAll(any(Pageable.class))).thenReturn(postPage);

        // Act
        Page<PostResponse> result = postService.getAllRecentPosts(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(testPostId, result.getContent().get(0).getId());
        verify(postRepository).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllRecentPosts_EmptyPage() {
        // Arrange
        Page<Post> emptyPage = new PageImpl<>(java.util.List.of());
        when(postRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        Page<PostResponse> result = postService.getAllRecentPosts(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        verify(postRepository).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllRecentPosts_WithPagination() {
        // Arrange
        Page<Post> postPage = new PageImpl<>(java.util.List.of(testPost));
        when(postRepository.findAll(any(Pageable.class))).thenReturn(postPage);

        // Act
        Page<PostResponse> result = postService.getAllRecentPosts(2, 5);

        // Assert
        assertNotNull(result);
        verify(postRepository).findAll(argThat(pageable -> 
            pageable.getPageNumber() == 2 && 
            pageable.getPageSize() == 5 &&
            pageable.getSort().stream().anyMatch(order -> 
                order.getProperty().equals("createdAt") && 
                order.getDirection() == Sort.Direction.DESC
            )
        ));
    }

    @Test
    void testUpdatePost_Success() {
        // Arrange
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        Optional<PostResponse> result = postService.updatePost(testPostId, updateRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testPostId, result.get().getId());
        verify(postRepository).findById(testPostId);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testUpdatePost_NotFound() {
        // Arrange
        UUID nonExistentPostId = UUID.randomUUID();
        when(postRepository.findById(nonExistentPostId)).thenReturn(Optional.empty());

        // Act
        Optional<PostResponse> result = postService.updatePost(nonExistentPostId, updateRequest);

        // Assert
        assertFalse(result.isPresent());
        verify(postRepository).findById(nonExistentPostId);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testUpdatePost_WithNullValues() {
        // Arrange
        PostUpdateRequest nullRequest = new PostUpdateRequest();
        // Don't set any values, leaving them null

        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        Optional<PostResponse> result = postService.updatePost(testPostId, nullRequest);

        // Assert
        assertTrue(result.isPresent());
        verify(postRepository).findById(testPostId);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testDeletePost_Success() {
        // Arrange
        when(postRepository.existsById(testPostId)).thenReturn(true);
        doNothing().when(postRepository).deleteById(testPostId);

        // Act
        boolean result = postService.deletePost(testPostId);

        // Assert
        assertTrue(result);
        verify(postRepository).existsById(testPostId);
        verify(postRepository).deleteById(testPostId);
    }

    @Test
    void testDeletePost_NotFound() {
        // Arrange
        UUID nonExistentPostId = UUID.randomUUID();
        when(postRepository.existsById(nonExistentPostId)).thenReturn(false);

        // Act
        boolean result = postService.deletePost(nonExistentPostId);

        // Assert
        assertFalse(result);
        verify(postRepository).existsById(nonExistentPostId);
        verify(postRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testDeletePost_WithNullId() {
        // Act
        boolean result = postService.deletePost(null);

        // Assert
        assertFalse(result);
        verify(postRepository, never()).existsById(any());
        verify(postRepository, never()).deleteById(any());
    }

    @Test
    void testConvertToDto_CompletePost() {
        // Arrange
        Post post = new Post();
        post.setId(testPostId);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setUserId(testUserId);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        when(postRepository.findById(testPostId)).thenReturn(Optional.of(post));

        // Act
        Optional<PostResponse> result = postService.getPostById(testPostId);

        // Assert
        assertTrue(result.isPresent());
        PostResponse dto = result.get();
        assertEquals(post.getId(), dto.getId());
        assertEquals(post.getTitle(), dto.getTitle());
        assertEquals(post.getContent(), dto.getContent());
        assertEquals(post.getUserId(), dto.getUserId());
        assertEquals(post.getCreatedAt(), dto.getCreatedAt());
        assertEquals(post.getUpdatedAt(), dto.getUpdatedAt());
    }
} 