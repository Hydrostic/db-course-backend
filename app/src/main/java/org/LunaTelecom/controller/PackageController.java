package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.LunaTelecom.dao.PackageDAO;
import org.LunaTelecom.dto.common.PagerRequest;
import org.LunaTelecom.dto.common.PagerResponse;
import org.LunaTelecom.dto.pack.UpdatePackageRequest;
import org.LunaTelecom.dto.pack.PackagePublic;
import org.LunaTelecom.http.ErrorResponse;
import org.LunaTelecom.http.SuccessResponse;
import org.LunaTelecom.http.validator.ValidatorUtils;
import org.LunaTelecom.infra.Database;
import org.LunaTelecom.model.Package;
import org.LunaTelecom.util.MoneyUtil;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collections;

public class PackageController extends Controller {
    public PackageController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.get("/package/list", this::listPackages);
        app.post("/package/add", this::addPackage);
        app.post("/package/update/{id}", this::updatePackage);
        app.post("/package/delete/{id}", this::deletePackage);
    }

    private void listPackages(Context ctx) {
        var pager = new PagerRequest(ctx);

        var packageDAO = jdbi.onDemand(PackageDAO.class);
        var q = packageDAO.count();
        var pages = (int)Math.ceil((double)q / pager.size);
        if (pager.getOffset() >= q) {
            ctx.json(new PagerResponse<>(Collections.emptyList(), pages, pager.size));
        }
        var df = new DecimalFormat("#.##");
        var datef = new DateTimeFormatterBuilder().appendPattern("yyyy/MM/dd HH:mm:ss").toFormatter();
        var packages = packageDAO.list(pager.getOffset(), pager.size).stream().map(pkg -> {
            return new PackagePublic(pkg.getId(),
                    pkg.getName(),
                    df.format((double)pkg.getPrice() / 100),
                    pkg.getCallAmount(),
                    pkg.getDataAmount(),
                    pkg.getCreatedAt().format(datef),
                    pkg.getUpdatedAt().format(datef));
        }).toList();
        ctx.json(new PagerResponse<>(packages, pages, pager.size));
    }
    private void addPackage(Context ctx) {
        UpdatePackageRequest req = ctx.bodyAsClass(UpdatePackageRequest.class);
        ValidatorUtils.validate(req);
        long priceCents = MoneyUtil.parsePriceToCents(req.price);
        var packageDAO = jdbi.onDemand(PackageDAO.class);
        var pkg = new Package();
        pkg.setPrice(priceCents);
        pkg.setName(req.name);
        pkg.setCallAmount(req.callAmount);
        pkg.setDataAmount(req.dataAmount);
        packageDAO.insert(pkg);
        new SuccessResponse().apply(ctx);
    }
    private void deletePackage(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        var packageDAO = jdbi.onDemand(PackageDAO.class);
        if(!packageDAO.delete(id)){
            throw new ErrorResponse("package not found", HttpStatus.BAD_REQUEST).asException();
        }
        new SuccessResponse().apply(ctx);
    }

    private void updatePackage(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        UpdatePackageRequest req = ctx.bodyAsClass(UpdatePackageRequest.class);
        ValidatorUtils.validate(req);
        long priceCents = MoneyUtil.parsePriceToCents(req.price);
        var packageDAO = jdbi.onDemand(PackageDAO.class);
        var pkg = new Package();
        pkg.setId(id);
        pkg.setPrice(priceCents);
        pkg.setName(req.name);
        pkg.setCallAmount(req.callAmount);
        pkg.setDataAmount(req.dataAmount);
        if (!packageDAO.update(pkg)) {
            throw new ErrorResponse("package not found", HttpStatus.BAD_REQUEST).asException();
        }
        new SuccessResponse().apply(ctx);
    }


}
