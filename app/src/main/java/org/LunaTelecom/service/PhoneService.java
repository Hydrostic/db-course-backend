package org.LunaTelecom.service;

import io.javalin.http.HttpStatus;
import org.LunaTelecom.http.ErrorResponse;
import org.LunaTelecom.model.NumberPool;

import java.util.List;
import java.util.Objects;

public class PhoneService {
    public static boolean checkRange(List<NumberPool> relatedPools, String start, String end, long parent) {

        for (var numberPool : relatedPools) {
            if (Objects.equals(numberPool.getId(), parent)) {
                if (!(numberPool.getStart().compareTo(start) <= 0 && end.compareTo(numberPool.getEnd()) <= 0)) {
                    return false;
                }
            } else {
                if (end.compareTo(numberPool.getStart()) >= 0 || start.compareTo(numberPool.getEnd()) <= 0) {
                    return false;
                }
            }
        }
        return true; // No overlap
    }
}
