package com.vividverse.post.repository;

import com.vividverse.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    // Spring Data JPA automatically provides methods like findAll(Pageable)
    // for pagination for the timeline/home page.
}