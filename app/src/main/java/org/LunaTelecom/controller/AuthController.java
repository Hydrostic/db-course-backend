package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.LunaTelecom.dao.AdminDao;
import org.LunaTelecom.dto.auth.LoginRequest;
import org.LunaTelecom.dto.auth.LoginResponse;
import org.LunaTelecom.http.ErrorResponse;
import org.LunaTelecom.http.validator.ValidatorUtils;
import org.LunaTelecom.http.validator.ValidationException;
import org.LunaTelecom.infra.Database;
import org.LunaTelecom.model.Admin;
import org.LunaTelecom.util.JWTUtil;
import org.LunaTelecom.util.PasswordUtil;

public class AuthController extends Controller {

    public AuthController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.post("/login", this::handleLogin);
    }

    private void handleLogin(Context ctx) throws ValidationException {
        LoginRequest loginRequest = ctx.bodyAsClass(LoginRequest.class);
        ValidatorUtils.validate(loginRequest);
        AdminDao adminDao = jdbi.onDemand(AdminDao.class);
        Admin a = adminDao.findByName(loginRequest.username);
        if (a == null) {
            ctx.json(new ErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED));
            return;
        }
        if (!PasswordUtil.verify(loginRequest.password, a.getPassword())) {
            ctx.json(new ErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED));
            return;
        }
        var token = JWTUtil.generateToken(a.getId().toString());
        ctx.json(new LoginResponse(token, a.getId(), a.getName()));
    }
}
