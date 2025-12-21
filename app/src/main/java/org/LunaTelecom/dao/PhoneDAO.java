package org.LunaTelecom.dao;

import org.LunaTelecom.model.PhoneAccount;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.LocalDateTime;
import java.util.List;

public interface PhoneDAO {
    @SqlQuery("SELECT COUNT(*) FROM phone_accounts WHERE deleted_at IS NULL")
    long countPhoneAccounts();

    @SqlQuery("SELECT * FROM phone_accounts WHERE deleted_at IS NULL ORDER BY id LIMIT :offset, :limit")
    @RegisterBeanMapper(PhoneAccount.class)
    List<PhoneAccount> listPhoneAccounts(@Bind("offset") long offset, @Bind("limit") int limit);

    @SqlUpdate("UPDATE phone_accounts SET owner = :owner, updated_at = :updatedAt, deleted_at = NULL WHERE phone_number = :phoneNumber ")
    void allocateNumber(PhoneAccount phone);

    @SqlUpdate("UPDATE phone_accounts SET deleted_at = :now WHERE phone_number = :phoneNumber")
    void freeNumber(String phoneNumber, LocalDateTime now);
    @SqlQuery("SELECT * FROM phone_accounts WHERE id = :id AND deleted_at IS NULL")
    @RegisterBeanMapper(PhoneAccount.class)
    PhoneAccount findById(@Bind("id") long id);

    // Return all phone numbers in the given inclusive range that are not deleted.
    @SqlQuery("SELECT phone_number FROM phone_accounts WHERE deleted_at IS NULL AND phone_number BETWEEN :start AND :end")
    List<String> listPhoneNumbersInRange(@Bind("start") String start, @Bind("end") String end);

    @SqlUpdate("UPDATE phone_accounts SET status = :status, updated_at = :updatedAt WHERE id = :id AND deleted_at IS NULL")
    int updateStatus(@Bind("id") long id, @Bind("status") String status, @Bind("updatedAt") LocalDateTime updatedAt);

    @SqlUpdate("UPDATE phone_accounts SET balance = :balance, updated_at = :updatedAt WHERE id = :id AND deleted_at IS NULL")
    int updateBalance(@Bind("id") long id, @Bind("balance") long balance, @Bind("updatedAt") LocalDateTime updatedAt);

    @SqlUpdate("UPDATE phone_accounts SET balance = balance - :amount, updated_at = :updatedAt WHERE id = :id AND deleted_at IS NULL")
    int decrementBalance(@Bind("id") long id, @Bind("amount") long amount, @Bind("updatedAt") LocalDateTime updatedAt);

    @SqlQuery("SELECT COUNT(*) FROM phone_accounts WHERE deleted_at IS NULL AND created_at >= :start AND created_at < :end")
    long countCreatedBetween(@Bind("start") LocalDateTime start, @Bind("end") LocalDateTime end);
}
