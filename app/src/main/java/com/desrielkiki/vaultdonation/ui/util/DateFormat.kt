package com.desrielkiki.vaultdonation.ui.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun customDateFormatToString(date: Date): String {
    val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
    val dayOfMonth = SimpleDateFormat("d", Locale.getDefault()).format(date)
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(date)
    val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)

    return "$dayOfWeek, $dayOfMonth, $monthName, $year"
}

fun formatSimpleDateToString(year: Int, month: Int, dayOfMonth: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth)

    val dateFormat = SimpleDateFormat("dd, MM, yyyy", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun formatDateToString(date: String): String {
    val inputDateFormat = SimpleDateFormat("dd, MM, yyyy", Locale.getDefault())
    val parsedDate = inputDateFormat.parse(date)

    val outputDateFormat = SimpleDateFormat("EEEE, d, MMMM, yyyy", Locale.getDefault())
    return outputDateFormat.format(parsedDate!!)
}

fun formatDate(date: Date, format: String): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(date)
}

/*
fun parseDate(dateString: String): Calendar? {
    val dateFormat = SimpleDateFormat("dd, MM, yyyy", Locale.getDefault())
    return try {
        val date = dateFormat.parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar
    } catch (e: Exception) {
        null
    }
}
*/
