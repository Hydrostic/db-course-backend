package org.LunaTelecom.dto.auth;

public class LoginResponse {
    public String token;
    public Long id;
    public String name;
    public String role;
    public long expirationSecs;

    public LoginResponse(String token, Long id, String name, String role, long expirationSecs) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.role = role;
        this.expirationSecs = expirationSecs;
    }
}
