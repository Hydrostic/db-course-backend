package org.LunaTelecom.dto.phone;

import jakarta.validation.constraints.NotBlank;

public class FreePhoneAccountRequest {
    @NotBlank
    public String phoneNumber;
}
