package com.example.blogproject.repository;

import com.example.blogproject.model.Post;
import com.example.blogproject.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post,Long> {
    List<Post> findAllByUserId(Long userId);
}
