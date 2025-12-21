package org.LunaTelecom.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Transaction {
    private Long id;
    private Long phone;
    /** price in cents */
    private Long price;
    private LocalDateTime createdAt;
}

