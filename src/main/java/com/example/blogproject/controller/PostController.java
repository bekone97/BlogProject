package com.example.blogproject.controller;

import com.example.blogproject.dto.LoadFile;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
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
                                @Valid @RequestBody PostDtoRequest postDtoRequest, Principal principal){
        log.info("Save new post by : {}",postDtoRequest);
        return postService.save(postDtoRequest,principal);
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
                                  @Valid @RequestBody PostDtoRequest postDtoRequest, Principal principal){
        log.info("Update post by id : {} and by : {}",postId,postDtoRequest);
        return postService.update(postId,postDtoRequest,principal);
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
                            @PathVariable @ValidId Long postId,Principal principal){
        log.info("Delete post by id: {}",postId);
        postService.deleteById(postId,principal);
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

    @PostMapping("/{postId}/file")
    @ResponseStatus(CREATED)
    public PostDtoResponse addFileToPost(@Parameter(description = "Id of post for file to be added",required = true)
                                         @PathVariable Long postId,
                                         @Parameter(description = "The file itself",required = true,schema = @Schema(implementation = MultipartFile.class))
                                         @RequestBody MultipartFile file, Principal principal){
        log.info("Added file to post with id : {} and with file content-type : {}",postId,file.getContentType());
        return postService.addFileToPost(postId,file,principal);
    }

    @PutMapping("/{postId}/file")
    @ResponseStatus(OK)
    public PostDtoResponse editFileToPost(@Parameter(description = "Id of post for file to be edited",required = true)
                                              @PathVariable Long postId,
                                          @Parameter(description = "The new file itself",required = true,schema = @Schema(implementation = MultipartFile.class))
                                              @RequestBody MultipartFile file, Principal principal){
        log.info("Edit file to post with id : {} and with file content-type : {}",postId,file.getContentType());
        return postService.editFileToPost(postId,file,principal);
    }

    @DeleteMapping("/{postId}/file")
    @ResponseStatus(OK)
    public void deleteFileFromPost(@Parameter(description = "Id of post for file to be deleted",required = true)
                                     @PathVariable Long postId, Principal principal){
        postService.deleteFileToPost(postId,principal);
    }

    @GetMapping("/{postId}/file")
    @ResponseStatus(OK)
    public ResponseEntity<ByteArrayResource> getFileFromPost(@Parameter(description = "Id of post for file to be deleted",required = true)
                                                        @PathVariable Long postId){
        log.info("Get file from post :{}",postId);
        LoadFile file = postService.getFileFromPost(postId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+file.getFileName()+"\"")
                .body(new ByteArrayResource(file.getFile()));
    }
}
