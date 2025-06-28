package com.vividverse.comment.repository;

import com.vividverse.comment.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends MongoRepository<Comment, String> {
    // Find comments by postId, ordered by creation date
    List<Comment> findByPostIdOrderByCreatedAtAsc(UUID postId);

    // Find replies to a specific parent comment
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(String parentCommentId);
}