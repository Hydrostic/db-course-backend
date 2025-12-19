package org.LunaTelecom.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class Admin {
    private Long id;
    private String name;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AdminRole role;
    public enum AdminRole {
        SUPER_ADMIN,
        OPERATOR;

    }

    public boolean isSuperAdmin() {
        return this.role == AdminRole.SUPER_ADMIN;
    }
}