package com.urbanoexpress.iridio3.pe.util;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DateUtils {

    public static long getLocalTimeStamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZonedDateTime localDateTime = instant.atZone(ZoneId.systemDefault());
        return localDateTime.toInstant().toEpochMilli();
    }

    public static ZonedDateTime getZonedDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return instant.atZone(ZoneId.systemDefault());
    }

    public static LocalDateTime parseHourToLocalDateTime(String hour) {
        try {
            if (hour != null) {
                String[] hourArray = hour.split(":");
                if (hourArray.length >= 2) {
                    LocalDateTime ldt = LocalDateTime.now(ZoneId.systemDefault());
                    ldt = ldt.withHour(Integer.parseInt(hourArray[0]))
                            .withMinute(Integer.parseInt(hourArray[1]))
                            .withSecond(0)
                            .withNano(0);
                    return ldt;
                }
            }
        } catch (DateTimeException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String format24HourTo12Hour(String hour) {
        LocalDateTime ldt = parseHourToLocalDateTime(hour);
        if (ldt != null) {
            return ldt.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
        } else {
            return hour;
        }
    }

    public static String formatFullDate(String date) {
        return formatFullDate(date, "dd/MM/yyyy");
    }

    public static String formatFullDate(String date, String inputFormat) {
        try {
            if (date != null) {
                LocalDate ld = LocalDate.parse(date, DateTimeFormatter.ofPattern(inputFormat));
                return ld.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
            }
        } catch (DateTimeException ex) {
            ex.printStackTrace();
        }
        return date;
    }

    public static String formatDate(String date, FormatStyle dateStyle) {
        return formatDate(date, dateStyle, "dd/MM/yyyy");
    }

    public static String formatDate(String date, FormatStyle dateStyle, String inputFormat) {
        try {
            if (date != null) {
                LocalDate ld = LocalDate.parse(date, DateTimeFormatter.ofPattern(inputFormat));
                return ld.format(DateTimeFormatter.ofLocalizedDate(dateStyle));
            }
        } catch (DateTimeException ex) {
            ex.printStackTrace();
        }
        return date;
    }
}
