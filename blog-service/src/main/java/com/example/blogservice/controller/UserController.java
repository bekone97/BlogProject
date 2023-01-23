package com.example.blogservice.controller;

import com.example.blogservice.dto.UserDtoRequest;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.handling.BlogApiErrorResponse;
import com.example.blogservice.handling.ValidationErrorResponse;
import com.example.blogservice.security.user.AuthenticatedUser;
import com.example.blogservice.service.UserService;
import com.example.blogservice.utils.ConstantUtil;
import com.example.blogservice.validator.ValidId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.blogservice.utils.ConstantUtil.SwaggerResponse.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @Operation(summary = "Returns all users")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType =APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))})
    })
    @GetMapping
    @ResponseStatus(OK)
    public Page<UserDtoResponse> getAllUsers(Pageable pageable) {
        log.info("Get all users");
        return userService.findAll(pageable);
    }

    @Operation(summary = "Returns a user by userId")
    @ApiResponses({
            @ApiResponse(responseCode =RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description =RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = BlogApiErrorResponse.class))})
    })
    @GetMapping("/{userId}")
    @ResponseStatus(OK)
    public UserDtoResponse getUserById(@Parameter(description = "Id of user to be searched", required = true, example = "1")
                                       @PathVariable @ValidId Long userId) {
        log.info("Get user by id : {}", userId);
        return userService.getById(userId);
    }

    @Operation(summary = "Save a new user")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_CREATED, description = RESPONSE_DESCRIPTION_CREATED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description =RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))})
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public UserDtoResponse save(@Parameter(description = "User information for a new user to be created", required = true,
            schema = @Schema(implementation = UserDtoRequest.class))
                                @Valid @RequestBody UserDtoRequest userDtoRequest,
                                @RequestParam @Size(min = 6, message = "User password can't be less than 6 symbols")
                                @NotBlank(message = "User's password can't be empty") String password) {
        log.info("Save user by : {}", userDtoRequest);
        return userService.save(userDtoRequest,password );
    }

    @Operation(summary = "Update an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType =APPLICATION_JSON,
                            schema = @Schema(implementation = BlogApiErrorResponse.class))})
    })
    @PutMapping("/{userId}")
    @ResponseStatus(OK)
    public UserDtoResponse update(@Parameter(description = "Id of user to be updated", required = true, example = "1")
                                  @PathVariable @ValidId Long userId,
                                  @Parameter(description = "User information for a user to be updated", required = true,
                                          schema = @Schema(implementation = UserDtoRequest.class))
                                  @Valid @RequestBody UserDtoRequest userDtoRequest,
                                  @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        log.info("Update user with id : {} by : {}", userId, userDtoRequest);
        return userService.update(userId, userDtoRequest,authenticatedUser);
    }


    @Operation(summary = "Delete an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = BlogApiErrorResponse.class))})
    })
    @DeleteMapping("/{userId}")
    @ResponseStatus(OK)
    public void deleteById(@Parameter(description = "Id of user to be deleted", required = true, example = "1")
                           @PathVariable @ValidId Long userId,
                           @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        log.info("Delete user by id");
        userService.deleteById(userId, authenticatedUser);
    }
}
