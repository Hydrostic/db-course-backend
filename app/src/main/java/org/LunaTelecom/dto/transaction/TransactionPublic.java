package org.LunaTelecom.dto.transaction;

import org.LunaTelecom.model.Transaction;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class TransactionPublic {
    public Long id;
    public Long phone;
    public Long price;
    public String createdAt;

    public TransactionPublic(Transaction tx) {
        DateTimeFormatter datef = new DateTimeFormatterBuilder().appendPattern("yyyy/MM/dd HH:mm:ss").toFormatter();
        this.id = tx.getId();
        this.phone = tx.getPhone();
        this.price = tx.getPrice();
        this.createdAt = tx.getCreatedAt() == null ? null : tx.getCreatedAt().format(datef);
    }
}

