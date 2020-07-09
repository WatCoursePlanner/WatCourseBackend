package com.watcourses.wat_courses.rules

object TermResolver {
    val ALL_TERMS_TO_VALUES_MAP = mapOf<String, Double>(
        "1A" to 1.0, "1B" to 1.5,
        "2A" to 2.0, "2B" to 2.5,
        "3A" to 3.0, "3B" to 3.5,
        "4A" to 4.0, "4B" to 4.5,
        "1st year" to 1.0,
        "2nd year" to 2.0,
        "3rd year" to 3.0,
        "4th year" to 4.0
    )

    fun getApplicableLabels(termName: String): Set<String> {
        val termValue = ALL_TERMS_TO_VALUES_MAP[termName] ?: throw Exception("term name $termName not recognized.")
        return ALL_TERMS_TO_VALUES_MAP.filter { it.value <= termValue }.keys.toSet()
    }
}