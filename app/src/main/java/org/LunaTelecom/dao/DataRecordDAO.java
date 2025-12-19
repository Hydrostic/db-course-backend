package org.LunaTelecom.dao;

import org.LunaTelecom.model.CallRecord;
import org.LunaTelecom.model.DataRecord;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlCall;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface DataRecordDAO {
    @SqlUpdate("INSERT INTO data_usage_record (phone_number, record_date, usage) VALUES (:phoneNumber, :date, :usage")
    void insert(DataRecord record);
}
