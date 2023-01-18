package com.example.blogproject.service.impl;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.mapper.PostMapper;
import com.example.blogproject.model.Post;
import com.example.blogproject.model.User;
import com.example.blogproject.repository.PostRepository;
import com.example.blogproject.service.PostService;
import com.example.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserService userService;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Override
    public Post getById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(()->new RuntimeException("There is no such post with id"));
    }

    public List<Post> findAll(){
        return postRepository.findAll();
    }

    @Override
    @Transactional
    public Post save(PostDtoRequest postDtoRequest) {
        log.info("Save post from postDroRequest:{}",postDtoRequest);
        UserDtoResponse user = userService.getById(postDtoRequest.getUserId());
        Post post = postMapper.mapToPost(postDtoRequest,user);
        post.setId(sequenceGeneratorService.generateSequence(post.SEQUENCE_NAME));
        return postRepository.save(post);

    }
}
