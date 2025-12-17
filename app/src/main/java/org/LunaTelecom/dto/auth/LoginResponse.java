package org.LunaTelecom.dto.auth;

public class LoginResponse {
    public String token;
    public Long id;
    public String name;

    public LoginResponse(String token, Long id, String name) {
        this.token = token;
        this.id = id;
        this.name = name;
    }
}
