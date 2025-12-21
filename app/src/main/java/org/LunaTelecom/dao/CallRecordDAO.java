package org.LunaTelecom.dao;

import org.LunaTelecom.model.CallRecord;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
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

    @SqlQuery(
        "SELECT COUNT(*) FROM (" +
        "  SELECT phone_number FROM call_records " +
        "  WHERE start_time >= :start AND start_time < :end " +
        "  GROUP BY phone_number HAVING COUNT(*) > :threshold" +
        ") t"
    )
    long countActivePhonesBetween(@Bind("start") java.time.LocalDateTime start,
                                 @Bind("end") java.time.LocalDateTime end,
                                 @Bind("threshold") long threshold);

    /**
     * Return list of idCard prefixes (2 digits) for active phones in the given time range.
     * Active = call count per phone_number > threshold.
     */
    @SqlQuery(
        "SELECT x.region_code AS region_code, COUNT(*) AS active_count " +
        "FROM (" +
        "  SELECT SUBSTRING(u.id_card, 1, 2) AS region_code " +
        "  FROM call_records cr " +
        "  JOIN phone_accounts pa ON pa.phone_number = cr.phone_number AND pa.deleted_at IS NULL " +
        "  JOIN users u ON u.id = pa.owner " +
        "  WHERE cr.start_time >= :start AND cr.start_time < :end " +
        "  GROUP BY cr.phone_number, SUBSTRING(u.id_card, 1, 2) " +
        "  HAVING COUNT(*) > :threshold" +
        ") x " +
        "GROUP BY x.region_code"
    )
    @RegisterBeanMapper(org.LunaTelecom.dto.stats.RegionActiveCountRow.class)
    List<org.LunaTelecom.dto.stats.RegionActiveCountRow> listActivePhonesRegionCountsBetween(
            @Bind("start") java.time.LocalDateTime start,
            @Bind("end") java.time.LocalDateTime end,
            @Bind("threshold") long threshold);
}
