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
    private static final int TOKEN_EXPIRATION_SECONDS = 3600; // 1 hour
    public AuthController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.post("/login", this::handleLogin);
    }

    private void handleLogin(Context ctx) {
        LoginRequest loginRequest = ctx.bodyAsClass(LoginRequest.class);
        ValidatorUtils.validate(loginRequest);
        AdminDao adminDao = jdbi.onDemand(AdminDao.class);
        Admin a = adminDao.findByName(loginRequest.username);
        if (a == null) {
            throw new ErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED).asException();
        }
        if (!PasswordUtil.verify(loginRequest.password, a.getPassword())) {
            throw new ErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED).asException();
        }
        var token = JWTUtil.generateToken(a.getId().toString(), TOKEN_EXPIRATION_SECONDS);
        ctx.json(new LoginResponse(token, a.getId(), a.getName(), a.getRole().toString(), TOKEN_EXPIRATION_SECONDS));
    }
}
