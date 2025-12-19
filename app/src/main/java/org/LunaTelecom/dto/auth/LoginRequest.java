package org.LunaTelecom.dto.auth;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public class LoginRequest {
    @NotBlank
    @Length(min = 1, max = 50)
    public String username;
    @NotBlank
    @Length(min = 1, max = 255)
    public String password;
}
