package org.LunaTelecom.dto.user;

import jakarta.validation.constraints.PositiveOrZero;

public class UserDeleteRequest {
    @PositiveOrZero
    public Long id;
}
