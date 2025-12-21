package org.LunaTelecom.dto.phone;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RandomNumberRequest {
    @Min(1)
    @Max(50)
    public Integer size;
    @Positive
    public Long pool;
}
