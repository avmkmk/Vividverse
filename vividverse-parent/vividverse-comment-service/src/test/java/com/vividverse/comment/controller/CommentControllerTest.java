package com.vividverse.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vividverse.comment.dto.CommentCreateRequest;
import com.vividverse.comment.dto.CommentResponse;
import com.vividverse.comment.service.CommentService;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private CommentCreateRequest createRequest;
    private CommentResponse commentResponse;
    private UUID testCommentId;
    private UUID testPostId;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testCommentId = UUID.randomUUID();
        testPostId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        
        createRequest = new CommentCreateRequest();
        createRequest.setPostId(testPostId);
        createRequest.setUserId(testUserId);
        createRequest.setContent("This is a new comment");
        createRequest.setParentCommentId(null);

        commentResponse = new CommentResponse();
        commentResponse.setId(testCommentId.toString());
        commentResponse.setPostId(testPostId);
        commentResponse.setUserId(testUserId);
        commentResponse.setContent("This is a test comment");
        commentResponse.setParentCommentId(null);
        commentResponse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testAddComment_Success() {
        // Arrange
        when(commentService.addComment(any(CommentCreateRequest.class))).thenReturn(commentResponse);

        // Act
        ResponseEntity<CommentResponse> response = commentController.addComment(createRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createRequest.getPostId(), response.getBody().getPostId());
        assertEquals(createRequest.getUserId(), response.getBody().getUserId());
        assertEquals(createRequest.getContent(), response.getBody().getContent());
    }

    @Test
    void testAddComment_WithParentComment() {
        // Arrange
        String parentCommentId = UUID.randomUUID().toString();
        createRequest.setParentCommentId(parentCommentId);
        
        CommentResponse replyResponse = new CommentResponse();
        replyResponse.setId(testCommentId.toString());
        replyResponse.setPostId(testPostId);
        replyResponse.setUserId(testUserId);
        replyResponse.setContent("This is a reply comment");
        replyResponse.setParentCommentId(parentCommentId);
        replyResponse.setCreatedAt(LocalDateTime.now());

        when(commentService.addComment(any(CommentCreateRequest.class))).thenReturn(replyResponse);

        // Act
        ResponseEntity<CommentResponse> response = commentController.addComment(createRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(parentCommentId, response.getBody().getParentCommentId());
    }

    @Test
    void testAddComment_WithNullValues() {
        // Arrange
        CommentCreateRequest nullRequest = new CommentCreateRequest();
        // Don't set any values, leaving them null

        when(commentService.addComment(any(CommentCreateRequest.class))).thenReturn(commentResponse);

        // Act
        ResponseEntity<CommentResponse> response = commentController.addComment(nullRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetCommentsForPost_Success() {
        // Arrange
        List<CommentResponse> comments = List.of(commentResponse);
        when(commentService.getCommentsForPost(testPostId)).thenReturn(comments);

        // Act
        ResponseEntity<List<CommentResponse>> response = commentController.getCommentsForPost(testPostId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testCommentId.toString(), response.getBody().get(0).getId());
        assertEquals(testPostId, response.getBody().get(0).getPostId());
    }

    @Test
    void testGetCommentsForPost_EmptyList() {
        // Arrange
        when(commentService.getCommentsForPost(testPostId)).thenReturn(List.of());

        // Act
        ResponseEntity<List<CommentResponse>> response = commentController.getCommentsForPost(testPostId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void testGetCommentsForPost_MultipleComments() {
        // Arrange
        CommentResponse comment1 = new CommentResponse();
        comment1.setId(UUID.randomUUID().toString());
        comment1.setPostId(testPostId);
        comment1.setUserId(testUserId);
        comment1.setContent("First comment");
        comment1.setCreatedAt(LocalDateTime.now().minusMinutes(5));

        CommentResponse comment2 = new CommentResponse();
        comment2.setId(UUID.randomUUID().toString());
        comment2.setPostId(testPostId);
        comment2.setUserId(testUserId);
        comment2.setContent("Second comment");
        comment2.setCreatedAt(LocalDateTime.now());

        List<CommentResponse> comments = List.of(comment1, comment2);
        when(commentService.getCommentsForPost(testPostId)).thenReturn(comments);

        // Act
        ResponseEntity<List<CommentResponse>> response = commentController.getCommentsForPost(testPostId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("First comment", response.getBody().get(0).getContent());
        assertEquals("Second comment", response.getBody().get(1).getContent());
    }

    @Test
    void testGetCommentsForPost_WithNullPostId() {
        // Act
        ResponseEntity<List<CommentResponse>> response = commentController.getCommentsForPost(null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(commentService).getCommentsForPost(null);
    }

    @Test
    void testAddComment_ServiceThrowsException() {
        // Arrange
        when(commentService.addComment(any(CommentCreateRequest.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            commentController.addComment(createRequest);
        });
    }

    @Test
    void testGetCommentsForPost_ServiceThrowsException() {
        // Arrange
        when(commentService.getCommentsForPost(testPostId))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            commentController.getCommentsForPost(testPostId);
        });
    }

    @Test
    void testAddComment_WithEmptyContent() {
        // Arrange
        createRequest.setContent("");

        when(commentService.addComment(any(CommentCreateRequest.class))).thenReturn(commentResponse);

        // Act
        ResponseEntity<CommentResponse> response = commentController.addComment(createRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testAddComment_WithVeryLongContent() {
        // Arrange
        String longContent = "A".repeat(1000);
        createRequest.setContent(longContent);

        CommentResponse longCommentResponse = new CommentResponse();
        longCommentResponse.setId(testCommentId.toString());
        longCommentResponse.setPostId(testPostId);
        longCommentResponse.setUserId(testUserId);
        longCommentResponse.setContent(longContent);
        longCommentResponse.setCreatedAt(LocalDateTime.now());

        when(commentService.addComment(any(CommentCreateRequest.class))).thenReturn(longCommentResponse);

        // Act
        ResponseEntity<CommentResponse> response = commentController.addComment(createRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(longContent, response.getBody().getContent());
    }

    @Test
    void testAddComment_WithSpecialCharacters() {
        // Arrange
        createRequest.setContent("This is a comment with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?");

        when(commentService.addComment(any(CommentCreateRequest.class))).thenReturn(commentResponse);

        // Act
        ResponseEntity<CommentResponse> response = commentController.addComment(createRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetCommentsForPost_NonExistentPost() {
        // Arrange
        UUID nonExistentPostId = UUID.randomUUID();
        when(commentService.getCommentsForPost(nonExistentPostId)).thenReturn(List.of());

        // Act
        ResponseEntity<List<CommentResponse>> response = commentController.getCommentsForPost(nonExistentPostId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }
} 