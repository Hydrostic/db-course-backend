package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.OkResponse;
import io.javalin.http.UnauthorizedResponse;
import org.LunaTelecom.dao.AdminDao;
import org.LunaTelecom.dto.admin.RegisterAdminRequest;
import org.LunaTelecom.http.ErrorResponse;
import org.LunaTelecom.http.SuccessResponse;
import org.LunaTelecom.http.validator.ValidationException;
import org.LunaTelecom.http.validator.ValidatorUtils;
import org.LunaTelecom.infra.Database;
import org.LunaTelecom.model.Admin;
import org.LunaTelecom.util.DBUtil;
import org.LunaTelecom.util.PasswordUtil;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

public class AdminController extends Controller {
    public AdminController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.post("/admin/register", this::registerAdmin);
    }

    private void registerAdmin(Context ctx) {
        RegisterAdminRequest req = ctx.bodyAsClass(RegisterAdminRequest.class);
        ValidatorUtils.validate(req);
        if (ctx.attribute("adminId") == null) {
            throw new ErrorResponse("unauthorized", HttpStatus.UNAUTHORIZED).asException();
        }
        var adminDao = jdbi.onDemand(AdminDao.class);
        var operator = adminDao.findById(ctx.attribute("adminId"));
        if (operator == null || !operator.isSuperAdmin()) {
            throw new UnauthorizedResponse("unauthorized");
        }
        Admin admin = new Admin();
        admin.setName(req.name);
        admin.setPassword(PasswordUtil.hash(req.password));
        admin.setRole(Admin.AdminRole.OPERATOR);
        try {

            adminDao.insert(admin);
        } catch (UnableToExecuteStatementException e) {
            if (DBUtil.isUniqueViolation(e)) {
                throw new ErrorResponse("username already exists", HttpStatus.BAD_REQUEST).asException();
            }
            throw e;
        }
        new SuccessResponse().apply(ctx);
    }
}
