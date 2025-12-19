package org.LunaTelecom.dto.pack;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

public class UpdatePackageRequest {
    @Length(min = 1, max = 50)
    public String name;
    @NotEmpty
    public String price;
    @PositiveOrZero
    public Long callAmount;
    @PositiveOrZero
    public Long dataAmount;
}
