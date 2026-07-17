package ec.edu.ups.icc.fundamentos01.security.dtos;

import java.util.Set;

public class AuthResponseDto {

    /*
     * Access token.
     * Se usa en:
     * Authorization: Bearer <token>
     */
    private String token;

    /*
     * Refresh token.
     * Se usa solo para:
     * POST /api/auth/refresh
     * POST /api/auth/logout
     */
    private String refreshToken;

    /*
     * Tipo de token.
     */
    private String type = "Bearer";

    private Long userId;

    private String name;

    private String email;

    private Set<String> roles;

    public AuthResponseDto() {
    }

    public AuthResponseDto(
            String token,
            String refreshToken,
            Long userId,
            String name,
            String email,
            Set<String> roles
    ) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.type = "Bearer";
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    public AuthResponseDto(
            String token,
            String refreshToken,
            String type,
            Long userId,
            String name,
            String email,
            Set<String> roles
    ) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.type = type;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(
            String token
    ) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(
            String refreshToken
    ) {
        this.refreshToken = refreshToken;
    }

    public String getType() {
        return type;
    }

    public void setType(
            String type
    ) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(
            Long userId
    ) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(
            Set<String> roles
    ) {
        this.roles = roles;
    }
}