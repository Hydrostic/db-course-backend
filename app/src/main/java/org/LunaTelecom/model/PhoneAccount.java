package org.LunaTelecom.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PhoneAccount {
    private Long id;
    private String phoneNumber;
    private byte status;
    private Long balance;
    private Long owner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
