package org.LunaTelecom.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Package {
    private long id;
    private String name;
    private long price;
    private long callAmount;
    private long dataAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
