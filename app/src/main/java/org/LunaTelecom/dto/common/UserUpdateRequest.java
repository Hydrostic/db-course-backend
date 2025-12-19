package org.LunaTelecom.dto.common;

import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @Size(max = 25, message = "姓名长度不能超过25个字符")
    private String name;

    @Size(min = 18, max = 18, message = "身份证号必须为18位")
    private String idCard;

    public UserUpdateRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
}
