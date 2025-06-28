package com.vividverse.post.service;

import com.vividverse.post.dto.PostCreateRequest;
import com.vividverse.post.dto.PostResponse;
import com.vividverse.post.dto.PostUpdateRequest;
import com.vividverse.post.entity.Post;
import com.vividverse.post.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUserId(request.getUserId()); // Assign the author
        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }

    @Transactional(readOnly = true)
    public Optional<PostResponse> getPostById(UUID id) {
        return postRepository.findById(id).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getAllRecentPosts(int page, int size) {
        // Sort by createdAt in descending order (most recent first)
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable).map(this::convertToDto);
    }

    @Transactional
    public Optional<PostResponse> updatePost(UUID id, PostUpdateRequest request) {
        return postRepository.findById(id).map(existingPost -> {
            existingPost.setTitle(request.getTitle());
            existingPost.setContent(request.getContent());
            Post updatedPost = postRepository.save(existingPost);
            return convertToDto(updatedPost);
        });
    }

    @Transactional
    public boolean deletePost(UUID id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private PostResponse convertToDto(Post post) {
        PostResponse dto = new PostResponse();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setUserId(post.getUserId());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }
}