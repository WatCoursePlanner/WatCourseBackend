package com.watcourses.wat_courses.utils

import com.watcourses.wat_courses.proto.CoopStream
import com.watcourses.wat_courses.proto.Schedule
import com.watcourses.wat_courses.proto.Term

// TODO: finish this to allow programs with multiple possible work term streams
// return a list of term names (e.g. 1A) indicating where the work term should be (after the terms).
fun CoopStream.getWorkTerms(): List<String> {
    return when (this) {
        CoopStream.NO_COOP -> listOf()
        CoopStream.STREAM_4 -> TODO()
        CoopStream.STREAM_8 -> TODO()
    }
}

fun <T> Sequence<T>.repeat() = sequence { while (true) yieldAll(this@repeat) }

fun Schedule.Companion.create(
    schedule: Schedule,
    startingYear: Int,
    @Suppress("UNUSED_PARAMETER") stream: CoopStream
): Schedule {
    var currentYear = startingYear
    var currentTerm = listOf(Term.FALL, Term.WINTER, Term.SPRING).asSequence().repeat()
    return Schedule(terms = schedule.terms.map {
        val result = it.copy(
            year = currentYear,
            term = currentTerm.first()
        )
        currentTerm = currentTerm.drop(1)
        if (currentTerm.first() == Term.SPRING) currentYear += 1
        result
    })
}