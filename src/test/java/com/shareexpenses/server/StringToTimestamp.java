package com.shareexpenses.server;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Slf4j
public class StringToTimestamp {

    @Test
    @SneakyThrows
    void convertsDateFromTransferWiseToTimestamp() {
        String wiseDate = "2022-10-09T09:37:33.460954Z";
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sd.setTimeZone(TimeZone.getTimeZone("UTC"));
        var r = sd.parse(wiseDate).getTime();
        log.info("Timestamp EXPENSES {}", r);
    }
}
