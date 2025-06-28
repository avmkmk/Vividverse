package com.vividverse.comment.service;

import com.vividverse.comment.dto.CommentCreateRequest;
import com.vividverse.comment.dto.CommentResponse;
import com.vividverse.comment.entity.Comment;
import com.vividverse.comment.repository.CommentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentResponse addComment(CommentCreateRequest request) {
        Comment comment = new Comment();
        comment.setPostId(request.getPostId());
        comment.setUserId(request.getUserId());
        comment.setContent(request.getContent());
        comment.setParentCommentId(request.getParentCommentId()); // Will be null for top-level

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    public List<CommentResponse> getCommentsForPost(UUID postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        return comments.stream()
                       .map(this::convertToDto)
                       .collect(Collectors.toList());
    }

    private CommentResponse convertToDto(Comment comment) {
        CommentResponse dto = new CommentResponse();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setUserId(comment.getUserId());
        dto.setParentCommentId(comment.getParentCommentId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
