package org.LunaTelecom.dto.phone;

import org.LunaTelecom.model.PhoneAccount;

import javax.swing.text.DateFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class PhonePublic {
    public Long id;
    public String phoneNumber;
    public String status;
    public Long balance;
    public Long owner;
    public String createdAt;
    public String updatedAt;
    public PhonePublic(PhoneAccount account) {
        DateTimeFormatter datef = new DateTimeFormatterBuilder().appendPattern("yyyy/MM/dd HH:mm:ss").toFormatter();
        this.id = account.getId();
        this.phoneNumber = account.getPhoneNumber();
        this.status = account.getStatus().toString();
        this.balance = account.getBalance();
        this.owner = account.getOwner();
        this.createdAt = account.getCreatedAt().format(datef);
        this.updatedAt = account.getUpdatedAt().format(datef);
    }
}
