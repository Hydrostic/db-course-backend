package org.LunaTelecom.model;

import java.time.LocalDateTime;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;

public class UserAccount {
    private long id;
    private String idCard;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserAccount() {
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getIdCard() {
        return idCard;
    }
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getupdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
