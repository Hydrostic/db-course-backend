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
    int countPhoneAccounts();

    @SqlQuery("SELECT * FROM phone_accounts WHERE deleted_at IS NULL ORDER BY id LIMIT :offset, :limit")
    @RegisterBeanMapper(PhoneAccount.class)
    List<PhoneAccount> listPhoneAccounts(@Bind("offset") long offset, @Bind("limit") int limit);

    @SqlUpdate("UPDATE phone_accounts SET owner = :owner, updated_at = :updatedAt, deleted_at = NULL WHERE phone_number = :phoneNumber ")
    void allocateNumber(PhoneAccount phone);

    @SqlUpdate("UPDATE phone_accounts SET deleted_at = :now WHERE phone_number = :phoneNumber")
    void freeNumber(String phoneNumber, LocalDateTime now);
}
