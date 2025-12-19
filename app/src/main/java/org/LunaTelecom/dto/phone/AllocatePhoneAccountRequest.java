package org.LunaTelecom.dto.phone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class AllocatePhoneAccountRequest {
    @NotBlank
    public String phoneNumber;
    @PositiveOrZero
    public long user;
}
