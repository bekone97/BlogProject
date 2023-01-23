package com.example.blogservice.controller;

import com.example.blogservice.dto.CommentDtoRequest;
import com.example.blogservice.dto.CommentDtoResponse;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.handling.BlogApiErrorResponse;
import com.example.blogservice.handling.ValidationErrorResponse;
import com.example.blogservice.security.user.AuthenticatedUser;
import com.example.blogservice.service.CommentService;
import com.example.blogservice.validator.ValidId;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.blogservice.utils.ConstantUtil.SwaggerResponse.*;


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
            @ApiResponse(responseCode =RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
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
                                          @Valid @RequestBody CommentDtoRequest commentDtoRequest,
                                          @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        log.info("Save new comment to post with id : {} by : {}", postId, commentDtoRequest);
        return commentService.save(commentDtoRequest, postId,authenticatedUser);
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
                                            @Valid @RequestBody CommentDtoRequest commentDtoRequest,
                                            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        log.info("Update comment from post with id :{} and with commentId : {} by : {}",postId,commentId,commentDtoRequest);
        return commentService.update(commentId,postId,commentDtoRequest,authenticatedUser);
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
                              @PathVariable @ValidId Long commentId,
                              @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        log.info("Delete comment with postId : {} and commentId: {}",postId,commentId);
        commentService.delete(commentId,postId,authenticatedUser);
    }
}
