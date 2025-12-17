package org.LunaTelecom.dto.auth;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public class LoginRequest {
    @NotBlank
    @Length(max = 255)
    public String username;
    @NotBlank
    @Length(max = 255)
    public String password;
}
