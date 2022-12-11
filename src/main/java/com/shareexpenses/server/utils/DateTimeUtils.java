package com.shareexpenses.server.utils;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.text.ParseException;
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
}
