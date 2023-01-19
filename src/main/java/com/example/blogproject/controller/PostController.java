package com.example.blogproject.controller;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.dto.PostDtoResponse;
import com.example.blogproject.handling.BlogApiErrorResponse;
import com.example.blogproject.handling.ValidationErrorResponse;
import com.example.blogproject.service.PostService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.blogproject.utils.ConstantUtil.SwaggerResponse.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PostController {

    private final PostService postService;

    @Operation(summary = "Returns all posts")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = PostDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))})
    })
    @GetMapping
    @ResponseStatus(OK)
    public Page<PostDtoResponse> findAllPosts(Pageable pageable){
        log.info("Find all posts");
        return postService.findAll(pageable);
    }


    @Operation(summary = "Returns a post by postId")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = PostDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = BlogApiErrorResponse.class))})
    })
    @GetMapping("/{postId}")
    @ResponseStatus(OK)
    public PostDtoResponse getPostById(@Parameter(description = "Id of post to be searched",required = true,example = "1")
                                        @PathVariable @ValidId Long postId){
        log.info("Get post by id:{}",postId);
        return postService.getById(postId);
    }



    @Operation(summary = "Save a new post")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_CREATED, description = RESPONSE_DESCRIPTION_CREATED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = PostDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))})
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public PostDtoResponse save(@Parameter(description = "Post information for a new post to be created", required = true,
            schema = @Schema(implementation = PostDtoRequest.class))
                                @Valid @RequestBody PostDtoRequest postDtoRequest){
        log.info("Save new post by : {}",postDtoRequest);
        return postService.save(postDtoRequest);
    }

    @Operation(summary = "Update an existing post")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = PostDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = BlogApiErrorResponse.class))})
    })
    @PutMapping("/{postId}")
    @ResponseStatus(OK)
    public PostDtoResponse update(@Parameter(description = "Id of post to be updated", required = true, example = "1")
                                    @PathVariable @ValidId Long postId,
                                  @Parameter(description = "Post information for a post to be updated", required = true,
                                          schema = @Schema(implementation = PostDtoRequest.class))
                                  @Valid @RequestBody PostDtoRequest postDtoRequest){
        log.info("Update post by id : {} and by : {}",postId,postDtoRequest);
        return postService.update(postId,postDtoRequest);
    }

    @Operation(summary = "Delete an existing post")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = BlogApiErrorResponse.class))})
    })
    @DeleteMapping("/{postId}")
    @ResponseStatus(OK)
    public void deleteById(@Parameter(description = "Id of post to be deleted", required = true, example = "1")
                            @PathVariable @ValidId Long postId){
        log.info("Delete post by id: {}",postId);
        postService.deleteById(postId);
    }

    @Operation(summary = "Returns posts of user by userId")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = PostDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = BlogApiErrorResponse.class))})
    })
    @GetMapping("/byUser/{userId}")
    @ResponseStatus(OK)
    public List<PostDtoResponse> findAllResponseByUserId(@Parameter(description = "Id of user for posts to be searched", required = true, example = "1")
                                                            @PathVariable @ValidId Long userId){
        log.info("Find all posts by user id : {}",userId);
        return postService.findAllByUserId(userId);
    }
}
