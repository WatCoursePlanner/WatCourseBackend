package com.watcourses.wat_courses.rules

enum class ConditionType { TRUE, FALSE, AND, OR, NOT, HAS_COURSE }

data class Condition(val type: ConditionType, val operands: List<Condition>) {
    companion object {
        fun parseFromText(text: String): Condition {
            return Condition(ConditionType.TRUE, emptyList())
        }
    }
}
