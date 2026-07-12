package com.commercecore.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing the response payload upon successful login.
 *
 * <p>Matches the frontend's LoginResponse structure:
 * <pre>{@code
 * interface LoginResponse {
 *   user: User;
 *   tokens: TokenResponse;
 * }
 * }</pre>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private UserDto user;
    private TokenResponse tokens;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        @Builder.Default
        private String tokenType = "Bearer";
    }

}
