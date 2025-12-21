package org.LunaTelecom.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PhoneAccount {
    private long id;
    private String phoneNumber;
    private PhoneStatus status;
    private long balance;
    private Long owner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public enum PhoneStatus {
        ACTIVE,
        SUSPENDED;
    }
}
