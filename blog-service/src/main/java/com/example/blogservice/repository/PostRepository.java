package com.example.blogservice.repository;

import com.example.blogservice.model.Comment;
import com.example.blogservice.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, Long> {
    List<Post> findAllByUserId(Long userId);

    boolean existsByIdAndCommentsContaining(Long postId, Comment comment);

    Optional<Post> findPostByCommentsIsContaining(Comment comment);
}
