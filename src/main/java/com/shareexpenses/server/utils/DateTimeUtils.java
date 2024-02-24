package com.shareexpenses.server.utils;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Component
public class DateTimeUtils {

    private static final String ISO_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");


    private static SimpleDateFormat getDateTimeFormatter() {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(ISO_DATE_PATTERN);
        dateTimeFormatter.setTimeZone(UTC_TIMEZONE);
        return dateTimeFormatter;
    }


    @SneakyThrows
    public Timestamp unixTimestampFromISOString(String isoDate) {
        return new Timestamp(getDateTimeFormatter().parse(isoDate).getTime());
    }

    /**
     * Converts a date of format "YYYY-MM-DD" to a timestamp after adding timing info (always 00:00:00) to it.
     */
    public Timestamp timestampFromDateStringWithoutTime(String isoDate) {
        return Timestamp.valueOf(isoDate + " 00:00:00");
    }

    /**
     * Adds time (and UTC timezone) to a date string without time.
     *
     * @param isoDate - format 'YYYY-MM-DD'
     * @return UTC date string - format 'YYYY-MM-DDT00:00:00.000Z'
     */
    public String dateStringToWiseString(String isoDate) {
        return isoDate + "T00:00:00.000Z";
    }
}
