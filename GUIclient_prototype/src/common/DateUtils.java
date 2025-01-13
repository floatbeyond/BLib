package common;

import java.sql.Date;
import java.time.LocalDate;

public class DateUtils{

    public static LocalDate toLocalDate(Date sqlDate) {
        return (sqlDate != null) ? sqlDate.toLocalDate() : null;
    }
}