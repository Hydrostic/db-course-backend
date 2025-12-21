package org.LunaTelecom.dao;

import org.LunaTelecom.model.Transaction;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface TransactionDAO {
    @SqlQuery("SELECT COUNT(*) FROM transcations")
    long count();

    @SqlQuery("SELECT * FROM transactions ORDER BY id DESC LIMIT :offset, :limit")
    @RegisterBeanMapper(Transaction.class)
    List<Transaction> list(@Bind("offset") long offset, @Bind("limit") int limit);

    @SqlQuery("SELECT COALESCE(SUM(price), 0) FROM transactions WHERE created_at >= :start AND created_at < :end")
    long sumPriceBetween(@Bind("start") java.time.LocalDateTime start, @Bind("end") java.time.LocalDateTime end);
}
