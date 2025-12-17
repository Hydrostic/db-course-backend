package org.LunaTelecom.dao;

import org.LunaTelecom.model.PhoneAccount;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface PhoneDAO {
    @SqlQuery("SELECT COUNT(*) FROM phone_accounts WHERE deleted_at IS NULL")
    int countPhoneAccounts();

    @SqlQuery("SELECT * FROM phone_accounts WHERE deleted_at IS NULL ORDER BY id LIMIT :offset, :limit")
    @RegisterBeanMapper(PhoneAccount.class)
    List<PhoneAccount> listPhoneAccounts(@Bind("offset") long offset, @Bind("limit") int limit);
}
