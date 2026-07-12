package com.commercecore.api.user.controller;

import com.commercecore.api.common.dto.ApiResponse;
import com.commercecore.api.user.dto.UserDto;
import com.commercecore.api.user.service.UserService;
import com.commercecore.api.user.repository.UserRepository;
import com.commercecore.api.user.mapper.UserMapper;
import com.commercecore.api.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Controller handling profile and user retrieval operations.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for managing user accounts and retrieving profiles")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserController(
            UserService userService,
            UserRepository userRepository,
            UserMapper userMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user profile", description = "Retrieves details of the authenticated user from session token context")
    public ResponseEntity<ApiResponse<UserDto>> getMyProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }

        String email = principal.getName();
        UserDto userDto = userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return ResponseEntity.ok(
                ApiResponse.success("Profile retrieved successfully", userDto)
        );
    }

}
