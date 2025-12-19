package org.LunaTelecom.dto.phone;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

public class UpdateNumberPoolRequest {

    @Length(min = 11, max = 11)
    @Pattern(regexp = "\\d+")
    public String start;
    @Length(min = 11, max = 11)
    @Pattern(regexp = "\\d+")
    public String end;
    @PositiveOrZero
    public Long parent;
    @Length(min = 1, max = 50)
    public String name;
}
