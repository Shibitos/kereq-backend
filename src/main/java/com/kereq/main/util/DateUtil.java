package com.kereq.main.util;

import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    private DateUtil() {}

    public static Date now() {
        return new Date();
    } //TODO: upgrade to newer api?

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

    public static Date addHours(Date date, long hours) {
        return new Date(date.getTime() + TimeUnit.HOURS.toMillis(hours));
    }

    public static Date addDays(Date date, long days) {
        return new Date(date.getTime() + TimeUnit.DAYS.toMillis(days));
    }

    public static int yearsBetween(Date fromDate, Date toDate) {
        DateTime from = new DateTime(fromDate.getTime());
        DateTime to = new DateTime(toDate.getTime());
        return Years.yearsBetween(from, to).getYears();
    }
}
