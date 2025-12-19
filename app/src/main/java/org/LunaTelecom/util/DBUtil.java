package org.LunaTelecom.util;

import java.sql.SQLException;

public final class DBUtil {
    public static boolean isUniqueViolation(Throwable e) {
        Throwable t = e;
        while (t != null) {
            if (t instanceof SQLException sqlEx) {
                return sqlEx.getErrorCode() == 1062;
            }
            t = t.getCause();
        }
        return false;
    }
}
