package common;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

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

    /**
     * Convert java.sql.Timestamp to java.time.LocalDateTime
     * @param timestamp
     * @return java.time.LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return (timestamp != null) ? timestamp.toLocalDateTime() : null;
    }

    /**
     * Convert java.time.LocalDateTime to java.sql.Timestamp
     * @param localDateTime
     * @return java.sql.Timestamp
     */
    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        return (localDateTime != null) ? Timestamp.valueOf(localDateTime) : null;
    }

    /**
     * Format a java.sql.Date object to a string
     * @param timestamp 
     * @param pattern - the pattern to format the date
     * @return String
     */
    public static String formatTimestamp(Timestamp timestamp, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return (timestamp != null) ? timestamp.toLocalDateTime().format(formatter) : null;
    }
}