package com.github.kawaiifoxx.inlinecalculator.utils

import java.util.*

object Utils {
    fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}
