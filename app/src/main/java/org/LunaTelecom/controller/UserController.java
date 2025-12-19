package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import org.LunaTelecom.dao.UserDAO;
import org.LunaTelecom.dto.common.PagerRequest;
import org.LunaTelecom.dto.common.PagerResponse;
import org.LunaTelecom.dto.common.UserCreateRequest;
import org.LunaTelecom.dto.common.UserDeleteRequest;
import org.LunaTelecom.dto.common.UserUpdateRequest;
import org.LunaTelecom.http.ErrorResponse;
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
        app.post("/uset/delete/{id}", this::deleteUserAccounts);
    }

    private void listUserAccounts(Context ctx) throws ValidationException {
        var pager = new PagerRequest(ctx);
        
        var userDao = jdbi.onDemand(UserDAO.class);
        var pages = userDao.countUserAccounts();

        if (pager.getOffset() >= pages) {
            ctx.json(new PagerResponse<>(Collections.emptyList(), pages, pager.size));
        }
        var users = userDao.listUsers(pager.getOffset(), pager.size);
        ctx.json(new PagerResponse<>(users, pages, pager.size));
    }

    private void createUserAccounts(Context ctx) throws ValidationException {
        UserCreateRequest request = ctx.bodyAsClass(UserCreateRequest.class);
        ValidatorUtils.validate(request);

        var userDao = jdbi.onDemand(UserDAO.class);

        var now = LocalDateTime.now();
        UserAccount user = new UserAccount();
        // user.setId(IdGenerator.nextId());
        user.setName(request.getName());
        user.setIdCard(request.getIdCard());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userDao.insert(user);

        ctx.status(200);
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

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            targetUser.setName(request.getName().trim());
        }
        if (request.getIdCard() != null && !request.getIdCard().trim().isEmpty()) {
            targetUser.setIdCard(request.getIdCard().trim());
        }
        targetUser.setUpdatedAt(LocalDateTime.now());
        userDao.update(targetUser);

        ctx.status(200);
    }

    private void deleteUserAccounts(Context ctx) throws ValidationException {
        UserDeleteRequest request = ctx.bodyAsClass(UserDeleteRequest.class);
        ValidatorUtils.validate(request);

        var userDao = jdbi.onDemand(UserDAO.class);
        userDao.delete(request.getId());
        ctx.status(200);   
    }
}
