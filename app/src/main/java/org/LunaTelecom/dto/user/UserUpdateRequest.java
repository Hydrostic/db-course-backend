package org.LunaTelecom.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @NotBlank
    @Size(max = 25, message = "姓名长度不能超过25个字符")
    public String name;
    
    @NotBlank
    @Size(min = 18, max = 18, message = "身份证号必须为18位")
    public String idCard;
}
