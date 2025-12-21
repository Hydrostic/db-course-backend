package org.LunaTelecom.service;

import org.LunaTelecom.dao.PackageDAO;
import org.LunaTelecom.dao.PhoneDAO;
import org.LunaTelecom.dao.SettlementDAO;
import org.jdbi.v3.core.Jdbi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Month-end settlement:
 * 1) For each active package_to_number mapping, deduct its price from the phone account balance (can go negative).
 * 2) Reset call_usage and data_usage for the new month.
 */
public final class MonthlySettlementService {
    private MonthlySettlementService() {}

    public static int settleMonthEnd(Jdbi jdbi, LocalDateTime now) {
        return jdbi.inTransaction(handle -> {
            var settlementDao = handle.attach(SettlementDAO.class);
            var monthKey = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            // Idempotency: if this month was already settled, don't charge again.
            try {
                settlementDao.tryLockMonth(monthKey);
            } catch (Exception e) {
                // Duplicate key / already inserted -> treat as already settled.
                return 0;
            }

            var packageDao = handle.attach(PackageDAO.class);
            var phoneDao = handle.attach(PhoneDAO.class);

            var mappings = packageDao.listActiveMappings(now);
            int charged = 0;

            for (var m : mappings) {
                // Deduct monthly price snapshot from package_to_number.price; balance can go negative.
                int updated = phoneDao.decrementBalance(m.getPhoneId(), m.getPrice(), now);
                if (updated > 0) {
                    charged++;
                }
                packageDao.resetUsageById(m.getId());
            }
            return charged;
        });
    }
}
