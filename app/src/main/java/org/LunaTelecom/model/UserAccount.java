package org.LunaTelecom.model;

import java.time.LocalDateTime;
 
import lombok.Data;

@Data
public class UserAccount {
    private long id;
    private String idCard;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
