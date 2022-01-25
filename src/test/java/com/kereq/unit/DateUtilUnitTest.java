package com.kereq.unit;

import com.kereq.common.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class DateUtilUnitTest {

    @Test
    void testAddMinutes() {
        Date now = new Date();
        Date plusFiveMinutes = new Date(now.getTime() + TimeUnit.MINUTES.toMillis(5));
        Date minusFiveMinutes = new Date(now.getTime() - TimeUnit.MINUTES.toMillis(5));

        assertThat(DateUtil.addMinutes(now, 5).getTime()).isEqualTo(plusFiveMinutes.getTime());
        assertThat(DateUtil.addMinutes(now, -5).getTime()).isEqualTo(minusFiveMinutes.getTime());
    }

    @Test
    void testAddHours() {
        Date now = new Date();
        Date plusFiveHours = new Date(now.getTime() + TimeUnit.HOURS.toMillis(5));
        Date minusFiveHours = new Date(now.getTime() - TimeUnit.HOURS.toMillis(5));

        assertThat(DateUtil.addHours(now, 5).getTime()).isEqualTo(plusFiveHours.getTime());
        assertThat(DateUtil.addHours(now, -5).getTime()).isEqualTo(minusFiveHours.getTime());
    }

    @Test
    void testAddDays() {
        Date now = new Date();
        Date plusFiveDays = new Date(now.getTime() + TimeUnit.DAYS.toMillis(5));
        Date minusFiveDays = new Date(now.getTime() - TimeUnit.DAYS.toMillis(5));

        assertThat(DateUtil.addDays(now, 5).getTime()).isEqualTo(plusFiveDays.getTime());
        assertThat(DateUtil.addDays(now, -5).getTime()).isEqualTo(minusFiveDays.getTime());
    }

    @Test
    void testIsExpired() {
        Date now = new Date();

        Date minusFiveMinutes = DateUtil.addMinutes(now, -5);
        Date plusFiveMinutes = DateUtil.addMinutes(now, 5);

        assertThat(DateUtil.isExpired(minusFiveMinutes, 6)).isFalse();
        assertThat(DateUtil.isExpired(minusFiveMinutes, 4)).isTrue();

        assertThat(DateUtil.isExpired(plusFiveMinutes)).isFalse();
        assertThat(DateUtil.isExpired(minusFiveMinutes)).isTrue();
    }

    @Test
    void testYearsBetween() {
        Date now = new Date();
        Date minusOne = new Date(now.getTime() - TimeUnit.DAYS.toMillis(367));
        Date minusTwo = new Date(now.getTime() - TimeUnit.DAYS.toMillis(367 * 2));

        assertThat(DateUtil.yearsBetween(minusOne, now)).isEqualTo(1);
        assertThat(DateUtil.yearsBetween(now, minusOne)).isEqualTo(-1);
        assertThat(DateUtil.yearsBetween(minusTwo, now)).isEqualTo(2);
        assertThat(DateUtil.yearsBetween(now, minusTwo)).isEqualTo(-2);
        assertThat(DateUtil.yearsBetween(minusTwo, minusOne)).isEqualTo(1);
        assertThat(DateUtil.yearsBetween(minusOne, minusTwo)).isEqualTo(-1);
        assertThat(DateUtil.yearsBetween(now, now)).isZero();
    }
}
