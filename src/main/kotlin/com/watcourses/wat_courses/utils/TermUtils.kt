package com.watcourses.wat_courses.utils

import com.watcourses.wat_courses.proto.Term

fun Term.getCode(year: Int): Int {
    val startMonth = when (this) {
        Term.SPRING -> "1"
        Term.WINTER -> "5"
        Term.FALL -> "9"
    }
    return "1${year.toString().substring(2)}$startMonth".toInt()
}