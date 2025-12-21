package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import org.LunaTelecom.dao.CallRecordDAO;
import org.LunaTelecom.dao.PhoneDAO;
import org.LunaTelecom.dao.TransactionDAO;
import org.LunaTelecom.dao.UserDAO;
import org.LunaTelecom.dao.PackageDAO;
import org.LunaTelecom.dto.stats.HomeStatsResponse;
import org.LunaTelecom.infra.Database;
import org.LunaTelecom.util.ProvinceUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class StatsController extends Controller {
    public StatsController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.get("/stats/home", this::homeStats);
        // package sales is included in /stats/home
    }

    private void homeStats(Context ctx) {
        var today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        var userDao = jdbi.onDemand(UserDAO.class);
        var phoneDao = jdbi.onDemand(PhoneDAO.class);
        var transactionDao = jdbi.onDemand(TransactionDAO.class);
        var callRecordDao = jdbi.onDemand(CallRecordDAO.class);
        var packageDao = jdbi.onDemand(PackageDAO.class);

        long todayNewUsers = userDao.countCreatedBetween(start, end);
        long todayNewPhones = phoneDao.countCreatedBetween(start, end);
        long todayIncome = transactionDao.sumPriceBetween(start, end);

        long threshold = 0;
        long todayActivePhones = callRecordDao.countActivePhonesBetween(start, end, threshold);

        var regionRows = callRecordDao.listActivePhonesRegionCountsBetween(start, end, threshold);
        var activePhonesByProvince = new HashMap<String, Long>();
        for (var row : regionRows) {
            var code = row.getRegionCode();
            if (code == null || code.length() < 2) {
                activePhonesByProvince.merge("未知", row.getActiveCount() == null ? 0L : row.getActiveCount(), Long::sum);
                continue;
            }
            // Province by first 2 digits of idCard
            var province = ProvinceUtil.RegionCode.getNameByCode(code.substring(0, 2));
            activePhonesByProvince.merge(province, row.getActiveCount() == null ? 0L : row.getActiveCount(), Long::sum);
        }

        // Monthly package sales (based on package_to_number.start_at in current month)
        var ym = java.time.YearMonth.from(today);
        var monthStart = ym.atDay(1).atStartOfDay();
        var monthEnd = ym.plusMonths(1).atDay(1).atStartOfDay();
        var salesRows = packageDao.countPackageSalesBetween(monthStart, monthEnd);
        Map<String, Long> monthlyPackageSales = new HashMap<>();
        for (var r : salesRows) {
            if (r.packageName == null) {
                continue;
            }
            monthlyPackageSales.put(r.packageName, r.sales == null ? 0L : r.sales);
        }

        ctx.json(new HomeStatsResponse(
                todayNewUsers,
                todayNewPhones,
                todayIncome,
                todayActivePhones,
                activePhonesByProvince,
                monthlyPackageSales
        ));
    }

    // remove packageSales endpoint
}
