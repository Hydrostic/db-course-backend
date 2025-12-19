package org.LunaTelecom.service;

import io.javalin.http.HttpStatus;
import org.LunaTelecom.dao.AdminDao;
import org.LunaTelecom.http.ErrorResponse;
import org.jdbi.v3.core.Jdbi;
import io.javalin.http.Context;

public class AdminService {
    public static void checkIsSuperAdmin(Context ctx, Jdbi jdbi) {

        var adminDao = jdbi.onDemand(AdminDao.class);
        var operator = adminDao.findById(ctx.attribute("adminId"));
        if (operator == null || !operator.isSuperAdmin()) {
            throw new ErrorResponse("unauthorized", HttpStatus.UNAUTHORIZED).asException();
        }
    }

}
