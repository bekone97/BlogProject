package com.example.blogproject.repository;

import com.example.blogproject.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment,Long> {
}
