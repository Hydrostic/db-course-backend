package org.LunaTelecom.dto.phone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class GetNewNumberRequset {
    @PositiveOrZero
    @NotBlank
    public long pool;
    @PositiveOrZero
    @NotBlank
    public int size;
}
