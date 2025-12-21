package org.LunaTelecom.dao;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/**
 * Settlement lock table access.
 *
 * Table requirement (MySQL-like):
 *   CREATE TABLE IF NOT EXISTS monthly_settlement_locks (
 *     settlement_month VARCHAR(7) PRIMARY KEY,
 *     created_at DATETIME NOT NULL
 *   );
 */
public interface SettlementDAO {
    @SqlUpdate("INSERT INTO monthly_settlement_locks (settlement_month, created_at) VALUES (:month, NOW())")
    void tryLockMonth(@Bind("month") String month);
}
