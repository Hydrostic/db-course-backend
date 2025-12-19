package org.LunaTelecom.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.LunaTelecom.dao.CallRecordDAO;
import org.LunaTelecom.dao.DataRecordDAO;
import org.LunaTelecom.dto.common.PagerRequest;
import org.LunaTelecom.dto.common.PagerResponse;
import org.LunaTelecom.dto.records.AddCallRecordRequest;
import org.LunaTelecom.dto.records.AddDateRecordRequest;
import org.LunaTelecom.dto.records.ListCallRecordResponse;
import org.LunaTelecom.infra.Database;
import org.LunaTelecom.model.CallRecord;
import org.LunaTelecom.model.DataRecord;

public class RecordController extends Controller {
    public RecordController(Javalin app) {
        super(app, Database.jdbi);
    }

    @Override
    void registerRoutes() {
        app.get("/call/list", this::listCallRecord);
        app.post("/call/report", this::addCallRecord);
        app.post("/data/daily-report", this::addDataRecord);
    }

    private List<ListCallRecordResponse> transRecordDataType(List<CallRecord> records) {
        List<ListCallRecordResponse> result = new ArrayList<>();;
        if (records.isEmpty())
            return null;
        for (var sourse : records) {
            var val = new ListCallRecordResponse();
            val.setId(sourse.getId());
            val.setPhoneNumber(sourse.getPhoneNumber());
            val.setEndType(sourse.getEndType());
            List<String> callPair = new ArrayList<>();
            callPair.add(sourse.getPhoneNumber());
            callPair.add(sourse.getCalledNumber());
            val.setCallPair(callPair);
            result.add(val);
        }
        return result;
    }
    
    private void listCallRecord(Context ctx) {
        var pager = new PagerRequest(ctx);

        var callRecordDAO = jdbi.onDemand(CallRecordDAO.class);
        var pages = callRecordDAO.count();
        if (pager.getOffset() >= pages) {
            ctx.json(new PagerResponse<>(Collections.emptyList(), pages, pager.size));
        }
        var records = callRecordDAO.listAllRecord(pager.getOffset(), pager.size);
        var responseRecord = transRecordDataType(records);
        ctx.json(new PagerResponse<>(responseRecord, pages, pager.size));
    }

    private void addCallRecord(Context ctx) {
        var requset = ctx.bodyAsClass(AddCallRecordRequest.class);

        var record = new CallRecord();
        record.setPhoneNumber(requset.callPair.getFirst());
        record.setCalledNumber(requset.callPair.getLast());
        var start = LocalDateTime.parse(requset.startTime);
        var end = LocalDateTime.parse(requset.endTime);
        record.setStartTime(start);
        record.setEndTime(end);
        record.setDuration(Duration.between(start, end).toSecondsPart());
        record.setEndType(requset.endType);

        var callRecordDAO = jdbi.onDemand(CallRecordDAO.class);

        callRecordDAO.insert(record);
    }

    private void addDataRecord(Context ctx) {
        var requset = ctx.bodyAsClass(AddDateRecordRequest.class);

        var record = new DataRecord();
        record.setPhoneNumber(requset.phoneNumber);
        record.setDate(LocalDate.parse(requset.date));
        record.setUsage(Double.parseDouble(requset.usage.trim()));

        var dateRecordDAO = jdbi.onDemand(DataRecordDAO.class);
        dateRecordDAO.insert(record);
    }
}