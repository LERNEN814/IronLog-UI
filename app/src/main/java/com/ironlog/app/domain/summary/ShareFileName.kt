package com.ironlog.app.domain.summary

import java.time.Instant
import java.time.ZoneId

/** M5-T5.3: file name of the exported share image. */
object ShareFileName {

    /** e.g. 1_700_000_000_000 in Asia/Shanghai -> IronLog_2023-11-15_0613.png */
    fun format(epochMillis: Long, zone: ZoneId): String {
        val local = Instant.ofEpochMilli(epochMillis).atZone(zone)
        val date = local.year.toString().padStart(4, '0') + "-" +
            local.monthValue.toString().padStart(2, '0') + "-" +
            local.dayOfMonth.toString().padStart(2, '0')
        val time = local.hour.toString().padStart(2, '0') +
            local.minute.toString().padStart(2, '0')
        return "IronLog_${date}_${time}.png"
    }
}
