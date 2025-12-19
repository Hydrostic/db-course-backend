package org.LunaTelecom.dto.admin;

public class AdminPublic {
    public Long id;
    public String name;
    public String role;
    public AdminPublic(long id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
}
