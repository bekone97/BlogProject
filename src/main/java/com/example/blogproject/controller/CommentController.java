package com.example.blogproject.controller;

import com.example.blogproject.dto.CommentDtoRequest;
import com.example.blogproject.dto.CommentDtoResponse;
import com.example.blogproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("posts/{postId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDtoResponse getCommentByPostIdAndCommentId(@PathVariable Long postId,
                                                             @PathVariable Long commentId){
        log.info("Get comment by commentId : {} and postId : {}",commentId,postId);
        return commentService.findCommentByPostIdAndCommentId(postId,commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDtoResponse> getCommentsByPostId(@PathVariable Long postId){
        log.info("Get all comments of post with id : {}",postId);
        return commentService.findAllCommentsByPost(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoResponse saveComment(@PathVariable Long postId,
            @RequestBody CommentDtoRequest commentDtoRequest){
        log.info("Save new comment to post with id : {} by : {}",postId,commentDtoRequest);
        return commentService.save(commentDtoRequest,postId);
    }

    @PutMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDtoResponse updateComment(@PathVariable Long postId,
                                            @PathVariable Long commentId,
                                            @RequestBody CommentDtoRequest commentDtoRequest){
        log.info("Update comment from post with id :{} and with commentId : {} by : {}",postId,commentId,commentDtoRequest);
        return commentService.update(commentId,postId,commentDtoRequest);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@PathVariable Long postId,
                              @PathVariable Long commentId){
        log.info("Delete comment with postId : {} and commentId: {}",postId,commentId);
        commentService.delete(commentId,postId);
    }
}
