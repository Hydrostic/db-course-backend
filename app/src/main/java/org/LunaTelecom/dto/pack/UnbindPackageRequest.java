package org.LunaTelecom.dto.pack;

import jakarta.validation.constraints.Positive;

public class UnbindPackageRequest {
    @Positive
    public Long id; // package_to_number id
}

