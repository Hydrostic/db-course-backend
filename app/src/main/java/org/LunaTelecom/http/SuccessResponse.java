package org.LunaTelecom.http;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class SuccessResponse {
    public String message;

    public SuccessResponse() {
        this.message = "ok";
    }

    public void apply(Context ctx) {
        ctx.status(HttpStatus.OK);
        ctx.json(this);
    }
}
