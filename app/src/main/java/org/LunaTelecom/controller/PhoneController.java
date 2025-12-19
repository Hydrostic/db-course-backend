package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.LunaTelecom.dao.NumberPoolDao;
import org.LunaTelecom.dao.PhoneDAO;
import org.LunaTelecom.dto.common.PagerRequest;
import org.LunaTelecom.dto.common.PagerResponse;
import org.LunaTelecom.dto.phone.AddNumberPoolRequest;
import org.LunaTelecom.dto.phone.AllocatePhoneAccountRequest;
import org.LunaTelecom.dto.phone.FreePhoneAccountRequest;
import org.LunaTelecom.dto.phone.GetNewNumberRequset;
import org.LunaTelecom.dto.phone.GetNewNumberResponse;
import org.LunaTelecom.http.ErrorResponse;
import org.LunaTelecom.http.SuccessResponse;
import org.LunaTelecom.http.validator.ValidationException;
import org.LunaTelecom.http.validator.ValidatorUtils;
import org.LunaTelecom.infra.Database;
import org.LunaTelecom.model.NumberPool;
import org.LunaTelecom.model.PhoneAccount;
import org.LunaTelecom.service.PhoneService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PhoneController extends Controller {
    public PhoneController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.get("/phone/list", this::listPhoneAccounts);
        app.get("/phone/random", this::getNewPhoneAccount);
        app.get("/phone/number-pool/list", this::listNumberPools);
        app.post("/phone/number-pool/add", this::addNumberPool);
        app.post("/phone/allocate", this::allocatePhoneAccount);
        app.post("/phone/free", this::freePhoneAccount);
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

    private List<String> generateRandomPhones(String start, String end, Long count) {
        long startNum = Long.parseLong(start);
        long endNum = Long.parseLong(end);
        long range = endNum - startNum + 1;

        Set<Long> selected = new HashSet<>();
        Random random = ThreadLocalRandom.current();

        while (selected.size() < count) {
            long num = startNum + random.nextLong(range);
            selected.add(num);
        }

        return selected.stream()
                .map(n -> String.format("%011d", n))  
                .sorted()  
                .collect(Collectors.toList());
    }

    private void getNewPhoneAccount(Context ctx) throws ValidationException {
        var request = ctx.bodyAsClass(GetNewNumberRequset.class);
        ValidatorUtils.validate(request);
        jdbi.inTransaction(handle -> {
            var numberPoolDao = handle.attach(NumberPoolDao.class);
            var freeNum = numberPoolDao.getFreeById(request.pool);
            if (freeNum < request.size) {
                new ErrorResponse("没有足够的空余号码", HttpStatus.INTERNAL_SERVER_ERROR).apply(ctx);
                return handle.rollback();
            }
            var numberPool = numberPoolDao.listAndLockById(request.pool);
            numberPoolDao.updateFree(request.pool, freeNum - request.size);
            var selectedNumber = generateRandomPhones(numberPool.getStart(), numberPool.getEnd(), request.size);
            var response = new GetNewNumberResponse(selectedNumber);
            ctx.json(response);
            return handle.commit();
        });
    }

    private void allocatePhoneAccount(Context ctx) throws ValidationException {
        var request = ctx.bodyAsClass(AllocatePhoneAccountRequest.class);
        ValidatorUtils.validate(request);
        var phoneAccountDao = jdbi.onDemand(PhoneDAO.class);
        var updateAccount = new PhoneAccount();
        updateAccount.setPhoneNumber(request.phoneNumber);
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
