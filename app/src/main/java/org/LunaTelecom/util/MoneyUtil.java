package org.LunaTelecom.util;

import org.LunaTelecom.http.validator.ValidationException;

import java.math.BigDecimal;

public class MoneyUtil {
    public static long parsePriceToCents(String price) throws ValidationException {
        try {
            if (price == null || price.isBlank()) {
                throw new ValidationException("price is required");
            }
            BigDecimal bd = new BigDecimal(price.trim()).multiply(BigDecimal.valueOf(100));
            return bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValueExact();
        } catch (NumberFormatException | ArithmeticException ex) {
            throw new ValidationException("invalid price format");
        }
    }

}
