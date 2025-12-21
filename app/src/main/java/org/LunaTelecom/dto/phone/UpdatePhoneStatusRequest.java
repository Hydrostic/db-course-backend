package org.LunaTelecom.dto.phone;

import jakarta.validation.constraints.NotNull;
import org.LunaTelecom.model.PhoneAccount;

public class UpdatePhoneStatusRequest {
    @NotNull
    public PhoneAccount.PhoneStatus status;
}

