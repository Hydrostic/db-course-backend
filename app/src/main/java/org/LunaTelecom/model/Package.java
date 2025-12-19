package org.LunaTelecom.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Package {
    private Long id;
    private String name;
    private Long price;
    private Long callAmount;
    private Long dataAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
