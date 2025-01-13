package common;

import java.sql.Date;
import java.time.LocalDate;

public class DateUtils {

    public static LocalDate toLocalDate(Date sqlDate) {
        return (sqlDate != null) ? sqlDate.toLocalDate() : null;
    }

    // Convert java.time.LocalDate to java.sql.Date
    public static Date toSqlDate(LocalDate localDate) {
        return (localDate != null) ? Date.valueOf(localDate) : null;
    }
}