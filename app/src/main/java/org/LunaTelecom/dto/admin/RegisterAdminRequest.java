package org.LunaTelecom.dto.admin;

import org.hibernate.validator.constraints.Length;

public class RegisterAdminRequest {
    @Length(min = 1, max = 50)
    public String name;
    @Length(min = 1, max = 255)
    public String password;
}
