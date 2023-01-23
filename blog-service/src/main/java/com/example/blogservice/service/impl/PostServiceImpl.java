package com.example.blogservice.service.impl;

import com.example.blogservice.dto.LoadFile;
import com.example.blogservice.dto.PostDtoRequest;
import com.example.blogservice.dto.PostDtoResponse;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.event.ModelCreatedEvent;
import com.example.blogservice.event.ModelDeletedEvent;
import com.example.blogservice.event.ModelType;
import com.example.blogservice.event.ModelUpdatedEvent;
import com.example.blogservice.exception.NotValidCredentialsException;
import com.example.blogservice.exception.ResourceNotFoundException;
import com.example.blogservice.mapper.PostMapper;
import com.example.blogservice.model.Comment;
import com.example.blogservice.model.Post;
import com.example.blogservice.model.User;
import com.example.blogservice.repository.PostRepository;
import com.example.blogservice.security.user.AuthenticatedUser;
import com.example.blogservice.service.FileService;
import com.example.blogservice.service.PostService;
import com.example.blogservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserService userService;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final FileService fileService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Cacheable(value = "post",key = "#id")
    public PostDtoResponse getById(Long id) {
        log.info("Get post by id : {}",id);
        return postRepository.findById(id)
                .map(this::getPostDtoResponse)
                .orElseThrow(()-> {
                    log.error("Post wasn't found by id : {}",id);
                    return new ResourceNotFoundException(Post.class,"id",id);
                });
    }



    @Override
    public Page<PostDtoResponse> findAll(Pageable pageable) {
        log.info("Find all posts");
        return postRepository.findAll(pageable != null ?
                        pageable :
                        PageRequest.of(1, 3, Sort.by("id")))
                .map((this::getPostDtoResponse));
    }

    @Override
    @Transactional
    public PostDtoResponse save(PostDtoRequest postDtoRequest, AuthenticatedUser authenticatedUser) {
        log.info("Save post from postDroRequest:{}",postDtoRequest);
        if (authenticatedUser==null)
            throw  new NotValidCredentialsException("User must be authenticated to save post");
        UserDtoResponse userDtoResponse = userService.getById(postDtoRequest.getUserId());
        Post post = getPostFromRequest(postDtoRequest, userDtoResponse);
        post=postRepository.save(post);
        publishSave(post);
        return getPostDtoResponse(post);
    }


    @Override
    @Transactional
    @CachePut(value = "post",key = "#postId")
    public PostDtoResponse update(Long postId, PostDtoRequest postDtoRequest, AuthenticatedUser authenticatedUser) {
        log.info("Check existing post by id : {} and update it by : {}", postId, postDtoRequest);
        UserDtoResponse userDtoResponse = userService.getById(postDtoRequest.getUserId());
        checkValidCredentials(userDtoResponse, authenticatedUser);
        return postRepository.findById(postId)
                .map(post -> getPostFromRequest(postId, postDtoRequest, userDtoResponse, post.getComments()))
                .map(postRepository::save)
                .map(post -> {
                    publishUpdate(postId);
                    return getPostDtoResponse(post);
                })
                .orElseThrow(() -> {
                    log.error("Post wasn't find by id : {}", postId);
                    return new ResourceNotFoundException(Post.class, "id", postId);
                });
    }

    @Override
    @Transactional
    @CacheEvict(value = "post",key = "#postId")
    public void deleteById(Long postId, AuthenticatedUser authenticatedUser) {
        log.info("Check existing post by id : {} and delete it",postId);
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()){
            throw new ResourceNotFoundException(Post.class,"id",postId);
        }
        Post post = optionalPost.get();
        checkValidCredentials(post.getUser(), authenticatedUser);
        applicationEventPublisher.publishEvent(ModelDeletedEvent.builder()
                        .modelType(ModelType.POST)
                        .model(post)
                .build());
        postRepository.delete(post);
    }

    @Override
    public List<PostDtoResponse> findAllByUserId(Long userId) {
        log.info("Find all posts by user id : {}",userId);
        if (userService.existsById(userId)){
            return postRepository.findAllByUserId(userId).stream()
                    .map(this::getPostDtoResponse)
                    .collect(Collectors.toList());
        }
        throw new ResourceNotFoundException(User.class,"userId",userId);
    }

    @Override
    public boolean existsById(Long postId) {
        log.info("Check existing by id : {} ",postId);
        return postRepository.existsById(postId);
    }

    @Override
    @Transactional
    public void addCommentToPost(Long postId, Comment newComment) {
       postRepository.findById(postId)
                .map(post -> {
                    if (post.getComments()==null){
                        post.setComments(List.of(newComment));
                    }else {
                        post.getComments().add(newComment);
                    }
                    publishUpdate(postId);
                    return postRepository.save(post);
                })
                .orElseThrow(() -> new ResourceNotFoundException(Post.class, "id", postId));
    }

    @Override
    public boolean existsByPostIdAndComment(Long postId, Comment comment) {
        return postRepository.existsByIdAndCommentsContaining(postId,comment);
    }

    @Override
    @Transactional
    public PostDtoResponse addFileToPost(Long postId, MultipartFile file, AuthenticatedUser authenticatedUser) {
        return postRepository.findById(postId)
                        .map(post -> {
                            checkValidCredentials(post.getUser(), authenticatedUser);
                            if (post.getFile()!=null)
                                throw new RuntimeException("File already exists");
                            post.setFile(fileService.uploadFile(file));
                            publishUpdate(postId);
                            return postRepository.save(post);
                        })
                .map(this::getPostDtoResponse)
                                .orElseThrow(()->new ResourceNotFoundException(Post.class,"id",postId));
    }

    @Override
    @Transactional
    public PostDtoResponse editFileToPost(Long postId, MultipartFile file, AuthenticatedUser authenticatedUser) {
        return postRepository.findById(postId)
                .map(post -> {
                    checkValidCredentials(post.getUser(), authenticatedUser);
                    if (post.getFile()==null)
                        throw new RuntimeException("File doesn't exist");
                    fileService.deleteFile(post.getFile());
                    post.setFile(fileService.uploadFile(file));
                    publishUpdate(postId);
                    return postRepository.save(post);
                })
                .map(this::getPostDtoResponse)
                .orElseThrow(()->new ResourceNotFoundException(Post.class,"id",postId));
    }

    @Override
    public void deleteFileToPost(Long postId, AuthenticatedUser authenticatedUser) {
        postRepository.findById(postId)
                .map(post -> {
                    checkValidCredentials(post.getUser(), authenticatedUser);
                    if (post.getFile()==null)
                        throw new RuntimeException("File doesn't exist");
                    fileService.deleteFile(post.getFile());
                    post.setFile(null);
                    publishUpdate(postId);
                    return postRepository.save(post);
                })
                .orElseThrow(()->new ResourceNotFoundException(Post.class,"id",postId));
    }

    private void publishUpdate(Long postId) {
        applicationEventPublisher.publishEvent(ModelUpdatedEvent.builder()
                .modelId(postId)
                .modelName(Post.class.getName())
                .build());
    }

    @Override
    public LoadFile getFileFromPost(Long postId) {
        return postRepository.findById(postId)
                .map(post -> {
                    if (post.getFile()==null)
                        throw new ResourceNotFoundException(LoadFile.class,"post",post);
                    return post.getFile();
                })
                .map(fileService::downloadFile)
                .orElseThrow(()-> new ResourceNotFoundException(Post.class,"id",postId));
    }

    @Override
    @Transactional
    public void deleteAllByUser(User user) {
        log.info("Delete all posts by user Id : {}",user);
        postRepository.findAllByUserId(user.getId()).stream()
                .peek(post->applicationEventPublisher.publishEvent(ModelDeletedEvent.builder()
                                .model(post)
                                .modelType(ModelType.POST)
                        .build()))
                .forEach(postRepository::delete);
    }

    @Override
    @Transactional
    public void deleteCommentFromPostByComment(Comment comment) {
        log.info("Delete comment from post with comment id : {}", comment);
        Optional<Post> optionalPost = postRepository.findPostByCommentsIsContaining(comment);
        if (optionalPost.isPresent()){
            Post post = optionalPost.get();
            List<Comment> collect = post.getComments().stream()
                    .filter(comment1 -> !comment1.equals(comment))
                    .collect(Collectors.toList());
            post.setComments(collect);
            postRepository.save(post);
        }
    }

    private PostDtoResponse getPostDtoResponse(Post post) {
        PostDtoResponse postDtoResponse = postMapper.mapToPostDtoResponse(post);
        if (post.getFile()!=null)
            postDtoResponse.setFile(fileService.downloadFile(post.getFile()));
        return postDtoResponse;
    }

    private void checkValidCredentials(UserDtoResponse userDtoResponse, AuthenticatedUser authenticatedUser) {
        if (authenticatedUser==null || !userDtoResponse.getUsername().equals(authenticatedUser.getUsername())||
                authenticatedUser.getAuthorities().stream().noneMatch(authority->authority.getAuthority().equals("ROLE_ADMIN"))){
            throw new NotValidCredentialsException("Credentials of principle are not valid");
        }
    }
    private void checkValidCredentials(User user, AuthenticatedUser authenticatedUser) {
        if (authenticatedUser==null ||!user.getUsername().equals(authenticatedUser.getUsername())||
                authenticatedUser.getAuthorities().stream().noneMatch(authority->authority.getAuthority().equals("ROLE_ADMIN"))){
            throw new NotValidCredentialsException("Credentials of principle are not valid");
        }
    }

    private void publishSave(Post post) {
        applicationEventPublisher.publishEvent(ModelCreatedEvent.builder()
                .modelId(post.getId())
                .modelName(Post.class.getName())
                .build());
    }

    private Post getPostFromRequest(PostDtoRequest postDtoRequest, UserDtoResponse userDtoResponse) {
        Post post = postMapper.mapToPost(sequenceGeneratorService.generateSequence(Post.SEQUENCE_NAME),
                postDtoRequest, userDtoResponse);
        if (postDtoRequest.getFile()!=null)
            post.setFile(fileService.uploadFile(postDtoRequest.getFile()));
        return post;
    }
    private Post getPostFromRequest(Long postId,PostDtoRequest postDtoRequest, UserDtoResponse userDtoResponse,
                                    List<Comment> comments) {
        Post post = postMapper.mapToPost(postId,
                postDtoRequest, userDtoResponse,comments);
        if (postDtoRequest.getFile()!=null)
            post.setFile(fileService.uploadFile(postDtoRequest.getFile()));
        return post;
    }

}
