package com.watcourses.wat_courses.utils

import com.watcourses.wat_courses.proto.Schedule

fun Schedule.Companion.build(terms: Map<String, List<String>>): Schedule {
    return Schedule(terms = terms.entries.map { Schedule.TermSchedule(termName = it.key, courseCodes = it.value) })
}