package com.vividverse.comment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vividverse.comment.dto.CommentCreateRequest;
import com.vividverse.comment.dto.CommentResponse;
import com.vividverse.comment.entity.Comment;
import com.vividverse.comment.repository.CommentRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment testComment;
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
        
        testComment = new Comment();
        testComment.setId(testCommentId.toString());
        testComment.setPostId(testPostId);
        testComment.setUserId(testUserId);
        testComment.setContent("This is a test comment");
        testComment.setParentCommentId(null);
        testComment.setCreatedAt(LocalDateTime.now());

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
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // Act
        CommentResponse result = commentService.addComment(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(createRequest.getPostId(), result.getPostId());
        assertEquals(createRequest.getUserId(), result.getUserId());
        assertEquals(createRequest.getContent(), result.getContent());
        assertEquals(createRequest.getParentCommentId(), result.getParentCommentId());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testAddComment_WithParentComment() {
        // Arrange
        String parentCommentId = UUID.randomUUID().toString();
        createRequest.setParentCommentId(parentCommentId);
        
        Comment commentWithParent = new Comment();
        commentWithParent.setId(testCommentId.toString());
        commentWithParent.setPostId(testPostId);
        commentWithParent.setUserId(testUserId);
        commentWithParent.setContent("This is a reply comment");
        commentWithParent.setParentCommentId(parentCommentId);
        commentWithParent.setCreatedAt(LocalDateTime.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(commentWithParent);

        // Act
        CommentResponse result = commentService.addComment(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(parentCommentId, result.getParentCommentId());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testAddComment_WithNullValues() {
        // Arrange
        CommentCreateRequest nullRequest = new CommentCreateRequest();
        // Don't set any values, leaving them null

        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // Act
        CommentResponse result = commentService.addComment(nullRequest);

        // Assert
        assertNotNull(result);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testGetCommentsForPost_Success() {
        // Arrange
        List<Comment> comments = List.of(testComment);
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(testPostId)).thenReturn(comments);

        // Act
        List<CommentResponse> result = commentService.getCommentsForPost(testPostId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCommentId.toString(), result.get(0).getId());
        assertEquals(testPostId, result.get(0).getPostId());
        assertEquals(testUserId, result.get(0).getUserId());
        verify(commentRepository).findByPostIdOrderByCreatedAtAsc(testPostId);
    }

    @Test
    void testGetCommentsForPost_EmptyList() {
        // Arrange
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(testPostId)).thenReturn(List.of());

        // Act
        List<CommentResponse> result = commentService.getCommentsForPost(testPostId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(commentRepository).findByPostIdOrderByCreatedAtAsc(testPostId);
    }

    @Test
    void testGetCommentsForPost_MultipleComments() {
        // Arrange
        Comment comment1 = new Comment();
        comment1.setId(UUID.randomUUID().toString());
        comment1.setPostId(testPostId);
        comment1.setUserId(testUserId);
        comment1.setContent("First comment");
        comment1.setCreatedAt(LocalDateTime.now().minusMinutes(5));

        Comment comment2 = new Comment();
        comment2.setId(UUID.randomUUID().toString());
        comment2.setPostId(testPostId);
        comment2.setUserId(testUserId);
        comment2.setContent("Second comment");
        comment2.setCreatedAt(LocalDateTime.now());

        List<Comment> comments = List.of(comment1, comment2);
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(testPostId)).thenReturn(comments);

        // Act
        List<CommentResponse> result = commentService.getCommentsForPost(testPostId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("First comment", result.get(0).getContent());
        assertEquals("Second comment", result.get(1).getContent());
        verify(commentRepository).findByPostIdOrderByCreatedAtAsc(testPostId);
    }

    @Test
    void testGetCommentsForPost_WithNullPostId() {
        // Act
        List<CommentResponse> result = commentService.getCommentsForPost(null);

        // Assert
        assertNotNull(result);
        verify(commentRepository).findByPostIdOrderByCreatedAtAsc(null);
    }

    @Test
    void testConvertToDto_CompleteComment() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(testCommentId.toString());
        comment.setPostId(testPostId);
        comment.setUserId(testUserId);
        comment.setContent("Test comment content");
        comment.setParentCommentId("parent123");
        comment.setCreatedAt(LocalDateTime.now());

        List<Comment> comments = List.of(comment);
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(testPostId)).thenReturn(comments);

        // Act
        List<CommentResponse> result = commentService.getCommentsForPost(testPostId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        CommentResponse dto = result.get(0);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getPostId(), dto.getPostId());
        assertEquals(comment.getUserId(), dto.getUserId());
        assertEquals(comment.getContent(), dto.getContent());
        assertEquals(comment.getParentCommentId(), dto.getParentCommentId());
        assertEquals(comment.getCreatedAt(), dto.getCreatedAt());
    }

    @Test
    void testAddComment_RepositoryThrowsException() {
        // Arrange
        when(commentRepository.save(any(Comment.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            commentService.addComment(createRequest);
        });
    }

    @Test
    void testGetCommentsForPost_RepositoryThrowsException() {
        // Arrange
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(testPostId))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            commentService.getCommentsForPost(testPostId);
        });
    }

    @Test
    void testAddComment_WithEmptyContent() {
        // Arrange
        createRequest.setContent("");

        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // Act
        CommentResponse result = commentService.addComment(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("", result.getContent());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testAddComment_WithVeryLongContent() {
        // Arrange
        String longContent = "A".repeat(1000);
        createRequest.setContent(longContent);

        Comment longComment = new Comment();
        longComment.setId(testCommentId.toString());
        longComment.setPostId(testPostId);
        longComment.setUserId(testUserId);
        longComment.setContent(longContent);
        longComment.setCreatedAt(LocalDateTime.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(longComment);

        // Act
        CommentResponse result = commentService.addComment(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(longContent, result.getContent());
        verify(commentRepository).save(any(Comment.class));
    }
} 