package com.zailaapp.zaila.extensions.projectOnly

import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMAT: String = "yyyy-MM-dd"


//JAN 01, 2019 - MAY 10, 2020
fun String.getDateExhibition(): String {

    val calendarDate = this.setDateExt()

    return "${calendarDate.getMonthExt()} ${calendarDate.getDayValueExt()}, ${calendarDate.getYearValueExt()}"
}


/**
 * Returns a Calendar instance based on the projects default format
 */
fun String.setDateExt(): Calendar {

    val c = Calendar.getInstance()
    val date = SimpleDateFormat(DATE_FORMAT).parse(this)

    c.time = date

    return c
}

/**
 * Returns the month string
 */
fun Calendar.getMonthExt(): String {

//    val result: String = getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "PT"))
    val result: String = getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())

    return result.substring(0, 1).toUpperCase() + result.substring(1, result.length)
}

/**
 * Returns the day value
 */
fun Calendar.getDayValueExt(): String {
    val result = "" + get(Calendar.DAY_OF_MONTH)

    return if (result.length > 1) result else "0" + result
}

/**
 * Returns the year value
 */
fun Calendar.getYearValueExt(): String {
    val result = "" + get(Calendar.YEAR)

    return if (result.length > 1) result else "0" + result
}