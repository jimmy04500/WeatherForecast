package com.edwardkim.weatherforecast

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun toLocalHourString(time: Long): String {
    return Instant
        .ofEpochSecond(time)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("ha"))
        .toString()
}

fun toDayOfWeekString(time: Long): String {
    return Instant
        .ofEpochSecond(time)
        .atZone(ZoneId.systemDefault())
        .dayOfWeek
        .toString()
}

fun toLocationString(name: String, state: String?, country: String): String =
    "${name}, " + (if (state == null) "" else "$state, ") + country

fun toLocationString(name: String, state: String?): String =
    "${name}, " + (if (state == null) "" else "$state, ")

fun toRegionString(state: String?, country: String): String =
    (if (state == null) "" else "$state, ") + country