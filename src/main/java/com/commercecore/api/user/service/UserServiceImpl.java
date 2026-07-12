package com.commercecore.api.user.service;

import com.commercecore.api.common.exception.BadRequestException;
import com.commercecore.api.common.exception.DuplicateResourceException;
import com.commercecore.api.common.exception.ResourceNotFoundException;
import com.commercecore.api.user.dto.LoginRequest;
import com.commercecore.api.user.dto.LoginResponse;
import com.commercecore.api.user.dto.RegisterRequest;
import com.commercecore.api.user.dto.UserDto;
import com.commercecore.api.user.entity.RefreshToken;
import com.commercecore.api.user.entity.User;
import com.commercecore.api.user.mapper.UserMapper;
import com.commercecore.api.user.repository.RefreshTokenRepository;
import com.commercecore.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service implementation containing all user business rules.
 *
 * <p>Annotated with {@code @Transactional} (readOnly = true by default)
 * to manage Hibernate transactions and database persistence contexts.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public UserServiceImpl(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {
        log.info("Registering user email={}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER); // Storefront registration default role

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email={}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.isActive()) {
            throw new BadRequestException("User account is inactive");
        }

        if (user.isBlocked()) {
            throw new BadRequestException("User account has been blocked: " + user.getBlockReason());
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = createAndSaveRefreshToken(user);

        return LoginResponse.builder()
                .user(userMapper.toDto(user))
                .tokens(LoginResponse.TokenResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())
                .build();
    }

    @Override
    @Transactional
    public LoginResponse.TokenResponse refreshToken(String refreshTokenStr) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (token.isRevoked() || token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token); // Cleanup expired/revoked token
            throw new BadRequestException("Refresh token has expired or been revoked");
        }

        User user = token.getUser();
        String newAccessToken = tokenService.generateAccessToken(user);

        return LoginResponse.TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token.getToken())
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshTokenStr) {
        refreshTokenRepository.findByToken(refreshTokenStr)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toDto(user);
    }

    /**
     * Creates and persists a new UUID refresh token for the user.
     */
    private String createAndSaveRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        return savedToken.getToken();
    }

}
