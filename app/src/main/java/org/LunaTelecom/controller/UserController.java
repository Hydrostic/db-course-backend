package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import org.LunaTelecom.dao.UserDAO;
import org.LunaTelecom.dto.common.PagerRequest;
import org.LunaTelecom.dto.common.PagerResponse;
import org.LunaTelecom.dto.user.UserCreateRequest;
import org.LunaTelecom.dto.user.UserDeleteRequest;
import org.LunaTelecom.dto.user.UserPublic;
import org.LunaTelecom.dto.user.UserUpdateRequest;
import org.LunaTelecom.http.ErrorResponse;
import org.LunaTelecom.http.SuccessResponse;
import org.LunaTelecom.http.validator.ValidationException;
import org.LunaTelecom.http.validator.ValidatorUtils;
import org.LunaTelecom.infra.Database;
import org.LunaTelecom.model.UserAccount;


public class UserController extends Controller{
    public UserController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.get("/user/list", this::listUserAccounts);
        app.post("/user/create", this::createUserAccounts);
        app.post("/user/update/{id}", this::updateUserAccounts);
        app.post("/user/delete/{id}", this::deleteUserAccounts);
    }

    private void listUserAccounts(Context ctx) throws ValidationException {
        var pager = new PagerRequest(ctx);
        
        var userDao = jdbi.onDemand(UserDAO.class);
        var pages = (int)Math.ceil((double)userDao.countUserAccounts() / pager.size);

        if (pager.getOffset() >= pages) {
            ctx.json(new PagerResponse<>(Collections.emptyList(), pages, pager.size));
        }
        var users = userDao.listUsers(pager.getOffset(), pager.size).stream().map(UserPublic::new).toList();
        ctx.json(new PagerResponse<>(users, pages, pager.size));
    }

    private void createUserAccounts(Context ctx) throws ValidationException {
        UserCreateRequest request = ctx.bodyAsClass(UserCreateRequest.class);
        ValidatorUtils.validate(request);

        var userDao = jdbi.onDemand(UserDAO.class);

        var now = LocalDateTime.now();
        UserAccount user = new UserAccount();
        // user.setId(IdGenerator.nextId());
        user.setName(request.name);
        user.setIdCard(request.idCard);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userDao.insert(user);
        new SuccessResponse().apply(ctx);
    }

    private void updateUserAccounts(Context ctx) throws ValidationException {
        long id = ctx.pathParamAsClass("id", Long.class).get();

        UserUpdateRequest request = ctx.bodyAsClass(UserUpdateRequest.class);
        ValidatorUtils.validate(request);

        var userDao = jdbi.onDemand(UserDAO.class);

        UserAccount targetUser = userDao.findById(id);
        if (targetUser == null) {
            ctx.json(new ErrorResponse("用户不存在", HttpStatus.NOT_FOUND));
        }

        if (request.name != null && !request.name.trim().isEmpty()) {
            targetUser.setName(request.name.trim());
        }
        if (request.idCard != null && !request.idCard.trim().isEmpty()) {
            targetUser.setIdCard(request.idCard.trim());
        }
        targetUser.setUpdatedAt(LocalDateTime.now());
        userDao.update(targetUser);
        new SuccessResponse().apply(ctx);
    }

    private void deleteUserAccounts(Context ctx) throws ValidationException {
//        UserDeleteRequest request = ctx.bodyAsClass(UserDeleteRequest.class);
//        ValidatorUtils.validate(request);
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        var userDao = jdbi.onDemand(UserDAO.class);
        userDao.delete(id);
        ctx.status(200);

        new SuccessResponse().apply(ctx);
    }
}
