package com.kereq.main.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    private DateUtil() {}

    public static Date now() {
        return new Date();
    }

    public static long timeDiffSince(Date date) {
        return now().getTime() - date.getTime();
    }

    public static boolean isExpired(Date startDate, long expirationTimeMin) {
        return timeDiffSince(startDate) > TimeUnit.MINUTES.toMillis(expirationTimeMin);
    }

    public static boolean isExpired(Date expireDate) {
        return now().getTime() >= expireDate.getTime();
    }

    public static Date addMinutes(Date date, long minutes) {
        return new Date(date.getTime() + TimeUnit.MINUTES.toMillis(minutes));
    }
}
