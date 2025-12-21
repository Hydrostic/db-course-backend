package org.LunaTelecom.dto.phone;

import jakarta.validation.constraints.PositiveOrZero;

public class UpdatePhoneBalanceRequest {
    @PositiveOrZero
    public Long balance; // stored in cents
}

