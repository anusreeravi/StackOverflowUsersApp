package com.candyspace.stackoverflowusers.utilities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.formatEpochToReadableDate(): String {
    if (this <= 0) return "N/A"
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(this * 1000))
    } catch (_: Exception) {
        "N/A"
    }
}
