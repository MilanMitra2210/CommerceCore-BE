package com.commercecore.api.user.service;

import com.commercecore.api.user.dto.LoginRequest;
import com.commercecore.api.user.dto.LoginResponse;
import com.commercecore.api.user.dto.RegisterRequest;
import com.commercecore.api.user.dto.UserDto;

/**
 * Service interface defining user business logic operations.
 */
public interface UserService {

    /**
     * Registers a new customer storefront user.
     */
    UserDto register(RegisterRequest request);

    /**
     * Authenticates a user and generates access/refresh tokens.
     */
    LoginResponse login(LoginRequest request);

    /**
     * Refreshes a user's access token using a valid refresh token.
     */
    LoginResponse.TokenResponse refreshToken(String refreshTokenStr);

    /**
     * Revokes the refresh token and signs the user out.
     */
    void logout(String refreshTokenStr);

    /**
     * Retrieves user profile details by ID.
     */
    UserDto getUserById(java.util.UUID id);

}
