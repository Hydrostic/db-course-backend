package org.LunaTelecom.interceptor;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import io.jsonwebtoken.Claims;
import org.LunaTelecom.http.ErrorResponse;
import org.LunaTelecom.util.JWTUtil;

public class AuthInterceptor implements Handler {

    public AuthInterceptor(Javalin app) {
        app.before(this);
    }

    @Override
    public void handle(Context ctx) {
        if (ctx.method().toString().equals("OPTIONS")) {
            ctx.status(HttpStatus.OK);
            return;
        }
        if (ctx.path().equals("/login")) {
            return;
        }
        String header = ctx.header("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ErrorResponse("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED).asException();
        }

        String token = header.substring(7).trim();
        long id;
        try{
            id = Long.parseLong(JWTUtil.validateTokenAndGetSubject(token));
        } catch (Exception e){
            throw new ErrorResponse("Invalid token", HttpStatus.UNAUTHORIZED).asException();
        }

        ctx.attribute("adminId", id);
    }
}
