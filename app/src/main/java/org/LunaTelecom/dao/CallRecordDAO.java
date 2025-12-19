package org.LunaTelecom.dao;

import org.LunaTelecom.model.CallRecord;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlCall;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface CallRecordDAO {
    @SqlQuery("SELECT COUNT(*) FROM call_records")
    int count();

    @SqlQuery("SELECT * FROM call_records LIMIT :offset, :limit")
    List<CallRecord> listAllRecord(@Bind("offset") long offset, @Bind("limit") int limit);
    
    @SqlUpdate("INSERT INTO call_records (phone_number, called_number, start_time, end_time, duration, end_type) " +
        "VALUES (:phoneNumber, :calledNumber, :startTime, :endTime, :duration, :endType)")
    void insert(CallRecord record);
}
