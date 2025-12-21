package org.LunaTelecom.service;

import io.javalin.http.HttpStatus;
import org.LunaTelecom.dao.PhoneDAO;
import org.LunaTelecom.http.ErrorResponse;
import org.LunaTelecom.model.NumberPool;
import org.jdbi.v3.core.Jdbi;

import java.util.*;
import java.util.regex.Pattern;

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
    public static List<String> generateRandomPhones(Jdbi jdbi, String start, String end, int size) {

        var phoneDao = jdbi.onDemand(PhoneDAO.class);
        List<String> existing = phoneDao.listPhoneNumbersInRange(start, end);
        Set<String> existingSet = new HashSet<>(existing);

        long s;
        long e;
        try {
            s = Long.parseLong(start);
            e = Long.parseLong(end);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("invalid number pool range");
        }
        if (s > e) {
            throw new IllegalArgumentException("invalid number pool range");
        }

        long total = e - s + 1;
        long available = total - existingSet.size();
        if (available < size) {
            size = (int)available;
        }

        var candidates = new HashSet<String>();
        while (candidates.size() < size) {
            Random r = new Random();
            String num = r.nextLong(s, e + 1) + "";
            if (!existingSet.contains(num)) {
                candidates.add(num);
            }
        }
        return candidates.stream().toList();
    }

    public static boolean checkPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^1[3456789]\\d{9}$");
        return pattern.matcher(phoneNumber).matches();
    }
}
