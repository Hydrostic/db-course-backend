package org.LunaTelecom.controller;

import io.javalin.Javalin;
import org.jdbi.v3.core.Jdbi;

public abstract class Controller {
    protected final Javalin app;
    protected final Jdbi jdbi;
    abstract void registerRoutes();
    public Controller(Javalin app, Jdbi jdbi) {
        this.app = app;
        this.jdbi = jdbi;
        registerRoutes();
    }
}
