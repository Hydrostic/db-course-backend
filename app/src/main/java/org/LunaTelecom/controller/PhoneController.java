package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.LunaTelecom.dao.NumberPoolDao;
import org.LunaTelecom.dao.PhoneDAO;
import org.LunaTelecom.dto.common.PagerRequest;
import org.LunaTelecom.dto.common.PagerResponse;
import org.LunaTelecom.dto.phone.AddNumberPoolRequest;
import org.LunaTelecom.http.ErrorResponse;
import org.LunaTelecom.http.validator.ValidationException;
import org.LunaTelecom.http.validator.ValidatorUtils;
import org.LunaTelecom.infra.Database;
import org.LunaTelecom.model.NumberPool;
import org.LunaTelecom.service.PhoneService;

import java.util.Collections;
import java.util.Objects;

public class PhoneController extends Controller {
    public PhoneController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.get("/phone/list", this::listPhoneAccounts);
        app.get("/phone/number-pool/list", this::listNumberPools);
        app.post("/phone/number-pool/add", this::addNumberPool);
    }

    private void listPhoneAccounts(Context ctx) {
        var pager = new PagerRequest(ctx);

        var phoneDao = jdbi.onDemand(PhoneDAO.class);
        var pages = phoneDao.countPhoneAccounts();
        if (pager.getOffset() >= pages) {
            ctx.json(new PagerResponse<>(Collections.emptyList(), pages, pager.size));
        }
        var phones = phoneDao.listPhoneAccounts(pager.getOffset(), pager.size);
        ctx.json(new PagerResponse<>(phones, pages, pager.size));
    }

    private void getNewPhoneAccount(Context ctx) throws ValidationException {

    }


    private void listNumberPools(Context ctx) {
        var numberPoolDao = jdbi.onDemand(NumberPoolDao.class);
        var pools = numberPoolDao.listAll();
        ctx.json(pools);
    }

    private void addNumberPool(Context ctx) {
        var req = ctx.bodyAsClass(AddNumberPoolRequest.class);
        ValidatorUtils.validate(req);
        jdbi.inTransaction(handle -> {
            var numberPoolDao = handle.attach(NumberPoolDao.class);
            var relatedNumberPool = numberPoolDao.listAndLockRelated(req.parent);

            if(!PhoneService.checkRange(relatedNumberPool, req.start, req.end, req.parent)) {
                new ErrorResponse("invalid range", HttpStatus.BAD_REQUEST).apply(ctx);
                return handle.rollback();
            }
            Long parentFree = relatedNumberPool.stream().filter(p -> p.getParent().equals(req.parent))
                    .findFirst().map(NumberPool::getFree).orElse(null);
            var newPool = new NumberPool();
            newPool.setStart(req.start);
            newPool.setEnd(req.end);
            newPool.setParent(req.parent);
            newPool.setFree(Long.parseLong(req.end) - Long.parseLong(req.start) + 1);
            newPool.setName(req.name);
            numberPoolDao.insert(newPool);
            if (parentFree != null && req.parent != 0) {
                var used = Long.parseLong(req.end) - Long.parseLong(req.start) + 1;
                var updatedFree = parentFree - used;
                numberPoolDao.updateFree(req.parent, updatedFree);
            }
            return handle.commit();
        });
    }


}
