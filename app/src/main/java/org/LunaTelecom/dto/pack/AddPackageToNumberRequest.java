package org.LunaTelecom.dto.pack;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;

public class AddPackageToNumberRequest {
    @Positive
    public Long phoneId;
    @Positive
    public Long packageId;

    @NotBlank
    @Pattern(regexp = "\\d{4}/\\d{2}/\\d{2}")
    public String endTime; // format: yyyy/MM/dd
}
