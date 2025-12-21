package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import org.LunaTelecom.dao.TransactionDAO;
import org.LunaTelecom.dto.common.PagerRequest;
import org.LunaTelecom.dto.common.PagerResponse;
import org.LunaTelecom.dto.transaction.TransactionPublic;
import org.LunaTelecom.infra.Database;

import java.util.Collections;

public class TransactionController extends Controller {
    public TransactionController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.get("/transaction/list", this::listTransactions);
    }

    private void listTransactions(Context ctx) {
        var pager = new PagerRequest(ctx);

        var transactionDao = jdbi.onDemand(TransactionDAO.class);
        var total = transactionDao.count();
        var pages = (int) Math.ceil((double) total / pager.size);

        if (pager.getOffset() >= total) {
            ctx.json(new PagerResponse<>(Collections.emptyList(), pages, pager.size));
            return;
        }

        var records = transactionDao.list(pager.getOffset(), pager.size)
                .stream()
                .map(TransactionPublic::new)
                .toList();
        ctx.json(new PagerResponse<>(records, pages, pager.size));
    }
}
