package com.ironlog.app.domain.summary

import com.google.common.truth.Truth.assertThat
import java.time.ZoneId
import org.junit.Test

class ShareFileNameTest {

    @Test
    fun formatsWithZonedDateTime() {
        assertThat(ShareFileName.format(1_700_000_000_000L, ZoneId.of("Asia/Shanghai")))
            .isEqualTo("IronLog_2023-11-15_0613.png")
    }

    @Test
    fun padsSingleDigitMonthDayHourMinute() {
        assertThat(ShareFileName.format(1_767_283_260_000L, ZoneId.of("Asia/Shanghai")))
            .isEqualTo("IronLog_2026-01-02_0001.png")
    }
}
