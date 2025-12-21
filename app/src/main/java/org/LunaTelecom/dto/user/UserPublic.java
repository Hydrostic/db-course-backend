package org.LunaTelecom.dto.user;
import org.LunaTelecom.model.UserAccount;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class UserPublic {
    public Long id;
    public String idCard;
    public String name;
    public String createdAt;
    public String updatedAt;

    public UserPublic(UserAccount account) {
        DateTimeFormatter datef =
                new DateTimeFormatterBuilder()
                        .appendPattern("yyyy/MM/dd HH:mm:ss")
                        .toFormatter();

        this.id = account.getId();
        this.idCard = account.getIdCard();
        this.name = account.getName();
        this.createdAt = account.getCreatedAt().format(datef);
        this.updatedAt = account.getUpdatedAt().format(datef);
    }
}
