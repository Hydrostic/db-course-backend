package org.LunaTelecom.dao;

import org.LunaTelecom.model.Package;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface PackageDAO {
    @SqlQuery("SELECT COUNT(*) FROM packages WHERE deleted_at IS NULL")
    int count();

    @SqlQuery("SELECT * FROM packages WHERE id = :id AND deleted_at IS NULL")
    @RegisterBeanMapper(Package.class)
    Package findById(@Bind("id") long id);

    @SqlQuery("SELECT * FROM packages WHERE deleted_at IS NULL LIMIT :offset, :limit")
    @RegisterBeanMapper(Package.class)
    List<Package> list(@Bind("offset") long offset, @Bind("limit") int limit);

    @SqlUpdate("INSERT INTO packages (name, price, call_amount, data_amount, created_at, updated_at) VALUES (:name, :price, :callAmount, :dataAmount, NOW(), NOW())")
    @RegisterBeanMapper(Package.class)
    void insert(@BindBean Package pkg);

    @SqlUpdate("UPDATE packages SET name = :name, price = :price, call_amount = :callAmount, data_amount = :dataAmount, updated_at = NOW() WHERE id = :id AND deleted_at IS NULL")
    @RegisterBeanMapper(Package.class)
    boolean update(@BindBean Package pkg);
    @SqlUpdate("UPDATE packages SET deleted_at = NOW() WHERE id = :id AND deleted_at IS NULL")
    boolean delete(@Bind("id") long id);
}
