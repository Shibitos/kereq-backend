package com.kereq.unit;

import com.kereq.main.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class DateUtilUnitTest {

    @BeforeEach
    public void setup() {
        //MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddMinutes() {
        Date now = new Date();
        Date plusFiveMinutes = new Date(now.getTime() + TimeUnit.MINUTES.toMillis(5));
        assertThat(DateUtil.addMinutes(now, 5).getTime()).isEqualTo(plusFiveMinutes.getTime());
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
        assertThat(DateUtil.yearsBetween(now, now)).isEqualTo(0);
    }
}
