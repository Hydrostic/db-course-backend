package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.LunaTelecom.dao.NumberPoolDao;
import org.LunaTelecom.dao.PhoneDAO;
import org.LunaTelecom.dto.common.PagerRequest;
import org.LunaTelecom.dto.common.PagerResponse;
import org.LunaTelecom.dto.phone.*;
import org.LunaTelecom.http.ErrorResponse;
import org.LunaTelecom.http.validator.ValidationException;
import org.LunaTelecom.http.validator.ValidatorUtils;
import org.LunaTelecom.infra.Database;
import org.LunaTelecom.model.NumberPool;
import org.LunaTelecom.model.PhoneAccount;
import org.LunaTelecom.service.PhoneService;

import java.time.LocalDateTime;
import java.util.Collections;

public class PhoneController extends Controller {
    public PhoneController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.get("/phone/list", this::listPhoneAccounts);
        app.get("/phone/random", this::generateRandomPhones);
        app.get("/phone/number-pool/list", this::listNumberPools);
        app.post("/phone/number-pool/add", this::addNumberPool);
        app.post("/phone/allocate", this::allocatePhoneAccount);
        app.post("/phone/free", this::freePhoneAccount);
        app.post("/phone/update/status/{id}", this::updatePhoneStatus);
        app.post("/phone/update/balance/{id}", this::updatePhoneBalance);
    }

    private void listPhoneAccounts(Context ctx) {
        var pager = new PagerRequest(ctx);

        var phoneDao = jdbi.onDemand(PhoneDAO.class);
        var pages = (int)Math.ceil((double)phoneDao.countPhoneAccounts() / pager.size);
        if (pager.getOffset() >= pages) {
            ctx.json(new PagerResponse<>(Collections.emptyList(), pages, pager.size));
        }
        var phones = phoneDao.listPhoneAccounts(pager.getOffset(), pager.size)
                .stream().map(PhonePublic::new).toList();
        ctx.json(new PagerResponse<>(phones, pages, pager.size));
    }

    private void generateRandomPhones(Context ctx) {
        RandomNumberRequest req = new RandomNumberRequest(
                ctx.queryParamAsClass("size", Integer.class).get(),
                ctx.queryParamAsClass("pool", Long.class).get()
        );
        ValidatorUtils.validate(req);
        var numberPoolDao = jdbi.onDemand(NumberPoolDao.class);
        var pool = numberPoolDao.findById(req.pool);
        if (pool == null) {
            throw new ErrorResponse("number pool not found", HttpStatus.BAD_REQUEST).asException();
        }
        var numbers = PhoneService.generateRandomPhones(jdbi, pool.getStart(), pool.getEnd(), req.size);
        ctx.json(numbers);
    }


    private void allocatePhoneAccount(Context ctx) throws ValidationException {
        var request = ctx.bodyAsClass(AllocatePhoneAccountRequest.class);
        ValidatorUtils.validate(request);
        var phoneAccountDao = jdbi.onDemand(PhoneDAO.class);
        var updateAccount = new PhoneAccount();
        // updateAccount.setPhoneNumber(request.phoneNumber);
        updateAccount.setOwner(request.user);
        var now = LocalDateTime.now();
        updateAccount.setUpdatedAt(now);
        phoneAccountDao.allocateNumber(updateAccount);
    }

    private void freePhoneAccount(Context ctx) throws ValidationException {
        var request = ctx.bodyAsClass(FreePhoneAccountRequest.class);
        var phoneAccountDao = jdbi.onDemand(PhoneDAO.class);
        phoneAccountDao.freeNumber(request.phoneNumber, LocalDateTime.now());
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
            Long parentFree = relatedNumberPool.stream().filter(p -> req.parent.equals(p.getId()))
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

    private void updatePhoneStatus(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();

        var req = ctx.bodyAsClass(UpdatePhoneStatusRequest.class);
        ValidatorUtils.validate(req);

        var phoneDao = jdbi.onDemand(PhoneDAO.class);
        var updated = phoneDao.updateStatus(id, req.status.name(), LocalDateTime.now());
        if (updated <= 0) {
            throw new ErrorResponse("phone not found", HttpStatus.BAD_REQUEST).asException();
        }
        ctx.status(HttpStatus.NO_CONTENT);
    }

    private void updatePhoneBalance(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();

        var req = ctx.bodyAsClass(UpdatePhoneBalanceRequest.class);
        ValidatorUtils.validate(req);

        var phoneDao = jdbi.onDemand(PhoneDAO.class);
        var updated = phoneDao.updateBalance(id, req.balance, LocalDateTime.now());
        if (updated <= 0) {
            throw new ErrorResponse("phone not found", HttpStatus.BAD_REQUEST).asException();
        }
        ctx.status(HttpStatus.NO_CONTENT);
    }
}
