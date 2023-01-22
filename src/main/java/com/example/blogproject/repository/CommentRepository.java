package com.example.blogproject.repository;

import com.example.blogproject.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment,Long> {
    Page<Comment> findAllByIdIn(List<Long> ids, Pageable pageable);
    List<Comment> findAllByUserId(Long userId);
}
