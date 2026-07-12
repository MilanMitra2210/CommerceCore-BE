package com.commercecore.api.user.controller;

import com.commercecore.api.common.dto.ApiResponse;
import com.commercecore.api.user.dto.LoginRequest;
import com.commercecore.api.user.dto.LoginResponse;
import com.commercecore.api.user.dto.RegisterRequest;
import com.commercecore.api.user.dto.UserDto;
import com.commercecore.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling public authentication and registration endpoints.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Authentication", description = "Endpoints for user signup, login, and token management")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new customer storefront profile")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody @Valid RegisterRequest request) {
        UserDto registeredUser = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", registeredUser));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Validates credentials and returns JWT access and refresh tokens")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse loginResponse = userService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", loginResponse)
        );
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Uses a valid refresh token to obtain a new short-lived access token")
    public ResponseEntity<ApiResponse<LoginResponse.TokenResponse>> refreshToken(@RequestBody @Valid LoginResponse.TokenResponse tokenRequest) {
        LoginResponse.TokenResponse tokenResponse = userService.refreshToken(tokenRequest.getRefreshToken());
        return ResponseEntity.ok(
                ApiResponse.success("Token refreshed successfully", tokenResponse)
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Revokes the active refresh token session")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LoginResponse.TokenResponse tokenRequest) {
        userService.logout(tokenRequest.getRefreshToken());
        return ResponseEntity.ok(
                ApiResponse.success("Logout successful")
        );
    }

}
