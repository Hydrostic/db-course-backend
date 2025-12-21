package org.LunaTelecom.service;

import org.jdbi.v3.core.Jdbi;
import org.tinylog.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Lightweight scheduler that checks hourly and performs settlement on the last day of month.
 *
 * We keep it dependency-free (no cron lib) and safe to run multiple times in the day by
 * gating on "last day" + "after 23:55".
 */
public final class MonthEndScheduler {
    private MonthEndScheduler() {}

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        var t = new Thread(r, "month-end-scheduler");
        t.setDaemon(true);
        return t;
    });

    public static void start(Jdbi jdbi) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                var today = LocalDate.now();
                boolean isLastDay = today.getDayOfMonth() == today.lengthOfMonth();
                if (!isLastDay) {
                    return;
                }

                var now = LocalDateTime.now();
                if (now.toLocalTime().isBefore(LocalTime.of(23, 55))) {
                    return;
                }

                int charged = MonthlySettlementService.settleMonthEnd(jdbi, now);
                if (charged == 0) {
                    Logger.info("Month-end settlement skipped or already settled for this month.");
                } else {
                    Logger.info("Month-end settlement completed. Charged mappings: {}", charged);
                }
            } catch (Exception e) {
                Logger.error(e, "Month-end settlement failed");
            }
        }, 0, 1, TimeUnit.HOURS);
    }
}
