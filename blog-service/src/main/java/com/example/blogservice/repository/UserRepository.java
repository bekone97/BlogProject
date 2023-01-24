package com.example.blogservice.repository;

import com.example.blogservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    boolean existsByIdAndUsername(Long id, String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndEmail(String username, String email);

    boolean existsByEmail(String email);
}
