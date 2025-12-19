package org.LunaTelecom.dao;

import org.LunaTelecom.model.Admin;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;

import java.util.List;

public interface AdminDao {

    @SqlUpdate("INSERT INTO admins (name, password, role, created_at, updated_at) VALUES (:name, :password, :role, NOW(), NOW())")
    @RegisterBeanMapper(Admin.class)
    void insert(@BindBean Admin admin);

    @SqlQuery("SELECT * FROM admins WHERE id = :id")
    @RegisterBeanMapper(Admin.class)
    Admin findById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM admins WHERE name = :name")
    @RegisterBeanMapper(Admin.class)
    Admin findByName(@Bind("name") String name);

    @SqlQuery("SELECT * FROM admins")
    @RegisterBeanMapper(Admin.class)
    List<Admin> findAll();

    @SqlUpdate("UPDATE admins SET name = :name, password = :password, updated_at = NOW() WHERE id = :id")
    void update(@BindBean Admin admin);

    @SqlUpdate("DELETE FROM admins WHERE id = :id")
    void delete(@Bind("id") Long id);
}