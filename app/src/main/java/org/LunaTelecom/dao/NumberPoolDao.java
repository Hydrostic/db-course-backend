package org.LunaTelecom.dao;

import org.LunaTelecom.model.NumberPool;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlCall;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface NumberPoolDao {
    @SqlQuery("SELECT count(*) FROM number_pools")
    long count();

    @SqlQuery("SELECT * FROM number_pools")
    @RegisterBeanMapper(NumberPool.class)
    List<NumberPool> listAll();

    @SqlQuery("SELECT * FROM number_pools WHERE parent = :parent")
    @RegisterBeanMapper(NumberPool.class)
    List<NumberPool> listByParent(Long parent);

    @SqlQuery("SELECT * FROM number_pools WHERE id = :id LIMIT 1")
    @RegisterBeanMapper(NumberPool.class)
    NumberPool findById(@Bind("id") Long id);

    @SqlQuery("SELECT start, end FROM number_pools WHERE parent = :parent OR id = :parent FOR UPDATE")
    @RegisterBeanMapper(NumberPool.class)
    List<NumberPool> listAndLockRelated(Long parent);

    @SqlQuery("SELECT * FROM number_pools WHERE id = :id FOR UPDATE")
    @RegisterBeanMapper(NumberPool.class)
    NumberPool listAndLockById(Long id);

    @SqlQuery("SELECT free FROM number_pools WHERE id = :id FOR UPDATE")
    Long getFreeById(Long id);

    @SqlUpdate("INSERT INTO number_pools (name, start, end, free, parent) VALUES (:name, :start, :end, :free, :parent)")
    void insert(NumberPool numberPool);

    @SqlUpdate("UPDATE number_pools SET free = :free WHERE id = :id")
    void updateFree(Long id, Long free);

}
