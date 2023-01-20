package com.example.blogproject.controller;

import com.example.blogproject.dto.CommentDtoRequest;
import com.example.blogproject.dto.CommentDtoResponse;
import com.example.blogproject.dto.UserDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.handling.BlogApiErrorResponse;
import com.example.blogproject.handling.ValidationErrorResponse;
import com.example.blogproject.service.CommentService;
import com.example.blogproject.validator.ValidId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.example.blogproject.utils.ConstantUtil.SwaggerResponse.*;

@RestController
@RequestMapping("posts/{postId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Returns a comment of certain post by postId and commentId")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = BlogApiErrorResponse.class))})
    })
    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDtoResponse getCommentByPostIdAndCommentId(@Parameter(description = "Id of post for comment to be searched",
                                                             required = true, example = "1")
                                                             @PathVariable @ValidId Long postId,
                                                             @Parameter(description = "Id of comment to be searched",
                                                                     required = true, example = "1")
                                                             @PathVariable @ValidId Long commentId) {
        log.info("Get comment by commentId : {} and postId : {}", commentId, postId);
        return commentService.findCommentByPostIdAndCommentId(postId, commentId);
    }

    @Operation(summary = "Returns all comments of certain post by its id")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = BlogApiErrorResponse.class))})
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<CommentDtoResponse> getCommentsByPostId(@Parameter(description = "Id of post for comments to be searched",
                                                        required = true, example = "1")
                                                            @PathVariable @ValidId Long postId, Pageable pageable){
        log.info("Get all comments of post with id : {}",postId);
        return commentService.findAllCommentsByPost(postId,pageable);
    }


    @Operation(summary = "Save a new comment for certain post")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_CREATED, description = RESPONSE_DESCRIPTION_CREATED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = BlogApiErrorResponse.class))})
    })

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoResponse saveComment(@Parameter(description = "Id of post for comment to be created",
            required = true, example = "1")
                                          @PathVariable @ValidId Long postId,
                                          @Parameter(description = "Comment information for a new comment to be created",
                                                  required = true,
                                                  schema = @Schema(implementation = CommentDtoRequest.class))
                                          @Valid @RequestBody CommentDtoRequest commentDtoRequest, Principal principal) {
        log.info("Save new comment to post with id : {} by : {}", postId, commentDtoRequest);
        return commentService.save(commentDtoRequest, postId,principal);
    }

    @Operation(summary = "Update an existing comment of certain post")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = BlogApiErrorResponse.class))})
    })
    @PutMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDtoResponse updateComment(@Parameter(description = "Id of post for comment to be updated",
                                            required = true, example = "1")
                                            @PathVariable @ValidId Long postId,
                                            @Parameter(description = "Id of comment to be updated",
                                                    required = true, example = "1")
                                            @PathVariable @ValidId Long commentId,
                                            @Parameter(description = "Comment information for a new comment to be created",
                                                    required = true,
                                                    schema = @Schema(implementation = CommentDtoRequest.class))
                                            @Valid @RequestBody CommentDtoRequest commentDtoRequest,Principal principal){
        log.info("Update comment from post with id :{} and with commentId : {} by : {}",postId,commentId,commentDtoRequest);
        return commentService.update(commentId,postId,commentDtoRequest,principal);
    }


    @Operation(summary = "Delete an existing comment of certain post")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = BlogApiErrorResponse.class))})
    })
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@Parameter(description = "Id of post for comment to be deleted",
                                required = true, example = "1")
                              @PathVariable @ValidId Long postId,
                              @Parameter(description = "Id of comment to be deleted",
                                      required = true, example = "1")
                              @PathVariable @ValidId Long commentId, Principal principal){
        log.info("Delete comment with postId : {} and commentId: {}",postId,commentId);
        commentService.delete(commentId,postId,principal);
    }
}
