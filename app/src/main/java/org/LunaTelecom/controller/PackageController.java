package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.LunaTelecom.dao.PackageDAO;
import org.LunaTelecom.dto.common.PagerRequest;
import org.LunaTelecom.dto.common.PagerResponse;
import org.LunaTelecom.dto.pack.PackageToNumberPublic;
import org.LunaTelecom.dto.pack.UpdatePackageRequest;
import org.LunaTelecom.dto.pack.PackagePublic;
import org.LunaTelecom.dto.pack.AddPackageToNumberRequest;
import org.LunaTelecom.dto.pack.UnbindPackageRequest;
import org.LunaTelecom.dto.pack.UpdatePackageEndRequest;
import org.LunaTelecom.http.ErrorResponse;
import org.LunaTelecom.http.SuccessResponse;
import org.LunaTelecom.http.validator.ValidatorUtils;
import org.LunaTelecom.infra.Database;
import org.LunaTelecom.model.Package;
import org.LunaTelecom.model.PackageToNumber;
import org.LunaTelecom.util.MoneyUtil;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collections;

public class PackageController extends Controller {
    public PackageController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.get("/package/list", this::listPackages);
        app.get("/package/number/{phoneId}", this::queryPackageOfNumber);
        app.post("/package/add", this::addPackage);
        app.post("/package/update/{id}", this::updatePackage);
        app.post("/package/delete/{id}", this::deletePackage);
        app.post("/package/number/add", this::addPackageToNumber);
        app.post("/package/number/unbind", this::unbindPackageFromNumber);
        app.post("/package/number/update-end", this::updatePackageEndTime);
    }

    private void listPackages(Context ctx) {
        var pager = new PagerRequest(ctx);

        var packageDAO = jdbi.onDemand(PackageDAO.class);
        var q = packageDAO.count();
        var pages = (int)Math.ceil((double)q / pager.size);
        if (pager.getOffset() >= q) {
            ctx.json(new PagerResponse<>(Collections.emptyList(), pages, pager.size));
            return;
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
    private void queryPackageOfNumber(Context ctx) {
        long numberId = ctx.pathParamAsClass("phoneId", Long.class).get();
        var phoneDAO = jdbi.onDemand(org.LunaTelecom.dao.PhoneDAO.class);
        var phoneAccount = phoneDAO.findById(numberId);
        if (phoneAccount == null) {
            throw new ErrorResponse("phone not found", HttpStatus.BAD_REQUEST).asException();
        }
        var packageDAO = jdbi.onDemand(PackageDAO.class);
        var pkgs = packageDAO.findPackagesByPhoneId(phoneAccount.getId());
        ctx.json(pkgs.stream().map(PackageToNumberPublic::new).toList());
    }

    private void addPackageToNumber(Context ctx) {
        AddPackageToNumberRequest req = ctx.bodyAsClass(AddPackageToNumberRequest.class);
        ValidatorUtils.validate(req);

        var phoneDAO = jdbi.onDemand(org.LunaTelecom.dao.PhoneDAO.class);
        var phoneAccount = phoneDAO.findById(req.phoneId);
        if (phoneAccount == null) {
            throw new ErrorResponse("phone not found", HttpStatus.BAD_REQUEST).asException();
        }

        var packageDAO = jdbi.onDemand(PackageDAO.class);
        var pkg = packageDAO.findById(req.packageId);
        if (pkg == null) {
            throw new ErrorResponse("package not found", HttpStatus.BAD_REQUEST).asException();
        }

        // create mapping
        var mapping = new PackageToNumber();
        mapping.setPhoneId(req.phoneId);
        mapping.setPkg(req.packageId);
        mapping.setPrice(pkg.getPrice());
        mapping.setCallAmount(pkg.getCallAmount());
        mapping.setDataAmount(pkg.getDataAmount());
        mapping.setCallUsage(0);
        mapping.setDataUsage(0);
        var now = LocalDateTime.now();
        mapping.setStartAt(now);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        try {
            var localDate = java.time.LocalDate.parse(req.endTime, df);
            mapping.setEndAt(LocalDateTime.of(localDate, LocalTime.MAX));
        } catch (Exception ex) {
            throw new ErrorResponse("invalid endTime format, expected yyyy/MM/dd", HttpStatus.BAD_REQUEST).asException();
        }

        packageDAO.insertPackageToNumber(mapping);
        new SuccessResponse().apply(ctx);
    }

    private void unbindPackageFromNumber(Context ctx) {
        UnbindPackageRequest req = ctx.bodyAsClass(UnbindPackageRequest.class);
        ValidatorUtils.validate(req);

        var packageDAO = jdbi.onDemand(PackageDAO.class);
        var mapping = packageDAO.findPackageToNumberById(req.id);
        if (mapping == null) {
            throw new ErrorResponse("mapping not found", HttpStatus.BAD_REQUEST).asException();
        }
        if (!packageDAO.deletePackageToNumber(req.id)) {
            throw new ErrorResponse("failed to unbind package", HttpStatus.INTERNAL_SERVER_ERROR).asException();
        }
        new SuccessResponse().apply(ctx);
    }

    private void updatePackageEndTime(Context ctx) {
        UpdatePackageEndRequest req = ctx.bodyAsClass(UpdatePackageEndRequest.class);
        ValidatorUtils.validate(req);

        var packageDAO = jdbi.onDemand(PackageDAO.class);
        var mapping = packageDAO.findPackageToNumberById(req.id);
        if (mapping == null) {
            throw new ErrorResponse("mapping not found", HttpStatus.BAD_REQUEST).asException();
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        try {
            var localDate = java.time.LocalDate.parse(req.endTime, df);
            var newEnd = LocalDateTime.of(localDate, LocalTime.MAX);
            if (!packageDAO.updatePackageToNumberEnd(req.id, newEnd)) {
                throw new ErrorResponse("failed to update end time", HttpStatus.INTERNAL_SERVER_ERROR).asException();
            }
        } catch (Exception ex) {
            throw new ErrorResponse("invalid endTime format, expected yyyy/MM/dd", HttpStatus.BAD_REQUEST).asException();
        }
        new SuccessResponse().apply(ctx);
    }

}
