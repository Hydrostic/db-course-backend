package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.LunaTelecom.dao.PhoneDAO;
import org.LunaTelecom.dto.common.PagerRequest;
import org.LunaTelecom.dto.common.PagerResponse;
import org.LunaTelecom.http.validator.ValidationException;
import org.LunaTelecom.infra.Database;

import java.util.Collections;

public class PhoneController extends Controller {
    public PhoneController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.get("/phone/list", this::listPhoneAccounts);
    }

    private void listPhoneAccounts(Context ctx) throws ValidationException {
        var pager = new PagerRequest(ctx);

        var phoneDao = jdbi.onDemand(PhoneDAO.class);
        var pages = phoneDao.countPhoneAccounts();
        if (pager.getOffset() >= pages) {
            ctx.json(new PagerResponse<>(Collections.emptyList(), pages, pager.size));
        }
        var phones = phoneDao.listPhoneAccounts(pager.getOffset(), pager.size);
        ctx.json(new PagerResponse<>(phones, pages, pager.size));

    }
}
