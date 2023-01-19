package com.example.blogproject.repository;

import com.example.blogproject.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken,Long> {
    Optional<RefreshToken> findRefreshTokenByToken(String token);
    List<RefreshToken> findRefreshTokensByUserId(Long id);
}
