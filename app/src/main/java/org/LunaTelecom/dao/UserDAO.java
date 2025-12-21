package org.LunaTelecom.dao;

import org.LunaTelecom.model.UserAccount;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;

import java.util.List;

public interface UserDAO {
    @SqlUpdate("INSERT INTO users (id_card, name, created_at, updated_at) VALUES (:idCard, :name, :createdAt, :updatedAt)")
    void insert(@BindBean UserAccount user);

    @SqlUpdate("UPDATE users SET name = :name, id_card = :idCard, updated_at = :updatedAt WHERE id = :id")
    void update(@BindBean UserAccount user);

    @SqlUpdate("DELETE FROM users WHERE id = :id")
    void delete(@Bind("id") long id);

    @SqlQuery("SELECT COUNT(*) FROM users")
    long countUserAccounts();

    @SqlQuery("SELECT * FROM users ORDER BY id LIMIT :offset, :limit")
    @RegisterBeanMapper(UserAccount.class)
    List<UserAccount> listUsers(@Bind("offset") long offset, @Bind("limit") int limit);

    @SqlQuery("SELECT * FROM users WHERE id = :id")
    UserAccount findById(@Bind("id") long id);    

    @SqlQuery("SELECT COUNT(*) FROM users WHERE created_at >= :start AND created_at < :end")
    long countCreatedBetween(@Bind("start") java.time.LocalDateTime start, @Bind("end") java.time.LocalDateTime end);
}
