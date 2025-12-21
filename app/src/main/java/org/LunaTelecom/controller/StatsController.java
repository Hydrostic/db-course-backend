package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import org.LunaTelecom.dao.PhoneDAO;
import org.LunaTelecom.dao.TransactionDAO;
import org.LunaTelecom.dao.UserDAO;
import org.LunaTelecom.dto.stats.HomeStatsResponse;
import org.LunaTelecom.infra.Database;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StatsController extends Controller {
    public StatsController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.get("/stats/home", this::homeStats);
    }

    private void homeStats(Context ctx) {
        var today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        var userDao = jdbi.onDemand(UserDAO.class);
        var phoneDao = jdbi.onDemand(PhoneDAO.class);
        var transactionDao = jdbi.onDemand(TransactionDAO.class);

        long todayNewUsers = userDao.countCreatedBetween(start, end);
        long todayNewPhones = phoneDao.countCreatedBetween(start, end);
        long todayIncome = transactionDao.sumPriceBetween(start, end);

        ctx.json(new HomeStatsResponse(todayNewUsers, todayNewPhones, todayIncome));
    }
}

