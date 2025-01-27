package common;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Utility class for converting between java.sql.Date and java.time.LocalDate.
 */
public class DateUtils {

    /**
     * Convert java.sql.Date to java.time.LocalDate
     * @param sqlDate
     * @return java.time.LocalDate
     */
    public static LocalDate toLocalDate(Date sqlDate) {
        return (sqlDate != null) ? sqlDate.toLocalDate() : null;
    }

    /**
     * Convert java.time.LocalDate to java.sql.Date
     * @param localDate
     * @return java.sql.Date
     */
    public static Date toSqlDate(LocalDate localDate) {
        return (localDate != null) ? Date.valueOf(localDate) : null;
    }
}