package com.kereq.unit;

import com.kereq.main.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class DateUtilUnitTest {

    @BeforeEach
    public void setup() {
        //MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddMinutes() {
        Date now = new Date();
        Date plusFiveMinutes = new Date(now.getTime() + TimeUnit.MINUTES.toMillis(5));
        assertThat(DateUtil.addMinutes(now, 5).getTime()).isEqualTo(plusFiveMinutes.getTime());
    }

    @Test
    public void testIsExpired() {
        Date now = new Date();

        Date minusFiveMinutes = DateUtil.addMinutes(now, -5);
        Date plusFiveMinutes = DateUtil.addMinutes(now, 5);

        assertThat(DateUtil.isExpired(minusFiveMinutes, 6)).isEqualTo(false);
        assertThat(DateUtil.isExpired(minusFiveMinutes, 4)).isEqualTo(true);

        assertThat(DateUtil.isExpired(plusFiveMinutes)).isEqualTo(false);
        assertThat(DateUtil.isExpired(minusFiveMinutes)).isEqualTo(true);
    }
}
