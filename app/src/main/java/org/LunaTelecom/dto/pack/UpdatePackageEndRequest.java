package org.LunaTelecom.dto.pack;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class UpdatePackageEndRequest {
    @Positive
    public Long id; // package_to_number id

    @NotBlank
    @Pattern(regexp = "\\d{4}/\\d{2}/\\d{2}")
    public String endTime; // format: yyyy/MM/dd
}
