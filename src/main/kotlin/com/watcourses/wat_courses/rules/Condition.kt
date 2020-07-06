package com.watcourses.wat_courses.rules

enum class ConditionType { TRUE, FALSE, AND, OR, NOT, HAS_COURSE }

data class Condition(val type: ConditionType, val operands: List<Condition>, val data: String? = null) {
    class ParseFailure(val reason: String, var str: String? = null) : Exception("Failed to parse $str because $reason")
    companion object {
        private fun courseSanityCheck(course: String) {
            if (course.split(" ").size != 2)
                throw ParseFailure("sanity check failed: $course does not look like a course")
        }

        // resolve "123" in CS 101, 123 to course("CS 123")
        private fun resolveCourse(parts: List<String>, index: Int): Condition {
            val part = parts[index]
            if (part.contains(" ")) { // e.g. CS 101. We have "CS" already so return directly
                courseSanityCheck(part)
                return course(part)
            }
            // look backwards for identifier
            val identifier = parts.subList(0, index).findLast { it.contains(" ") }?.substringBefore(" ")
                ?: ParseFailure("Can't find a course identifier")
            val completeCourseCode = "$identifier $part"
            courseSanityCheck(completeCourseCode)
            return course(completeCourseCode)
        }

        /*
         * Example: CS 101, 123, CS 102/ECE 123, CS 233 => AND(CS101, CS123, OR(CS102, ECE123), CS233)
         */
        fun parseFromText(text: String): Condition {
            try {
                val processedText = text.substringAfter(":").trim()
                val andParts = processedText.split(",").map { it.trim() }
                return Condition(ConditionType.AND, andParts.mapIndexed { i, part ->
                    if (part.contains("/")) {
                        val orParts = part.split("/")
                        Condition(ConditionType.OR, orParts.mapIndexed { j, _ -> resolveCourse(orParts, j) })
                    } else
                        resolveCourse(andParts, i)
                })
            } catch (e: ParseFailure) {
                e.str = text
                throw e
            }
        }

        fun and(vararg conditions: Condition) = Condition(ConditionType.AND, conditions.toList())
        fun or(vararg conditions: Condition) = Condition(ConditionType.OR, conditions.toList())
        fun not(condition: Condition) = Condition(ConditionType.NOT, listOf(condition))
        fun alwaysTrue() = Condition(ConditionType.TRUE, listOf())
        fun alwaysFalse() = Condition(ConditionType.FALSE, listOf())
        fun course(code: String) = Condition(ConditionType.HAS_COURSE, listOf(), data = code)
    }

    fun check(courseCodeSet: Set<String>): Boolean {
        return when (type) {
            ConditionType.TRUE -> true
            ConditionType.FALSE -> false
            ConditionType.AND -> operands.map { it.check(courseCodeSet) }.reduce { a, b -> a && b }
            ConditionType.OR -> operands.map { it.check(courseCodeSet) }.reduce { a, b -> a || b }
            ConditionType.NOT -> !operands.single().check(courseCodeSet)
            ConditionType.HAS_COURSE -> courseCodeSet.contains(data)
        }
    }

    private fun toStringInternal(): String {
        val bracketIfComplex = { c: String -> if (c.contains("&&") || c.contains("||")) "($c)" else c }
        return when (type) {
            ConditionType.TRUE -> "true"
            ConditionType.FALSE -> "false"
            ConditionType.AND -> bracketIfComplex(operands.joinToString(" && ") { it.toStringInternal() })
            ConditionType.OR -> bracketIfComplex(operands.joinToString(" || ") { it.toStringInternal() })
            ConditionType.NOT -> "!${operands.single().toStringInternal()}"
            ConditionType.HAS_COURSE -> "$data"
        }
    }

    override fun toString(): String {
        return toStringInternal().trimStart('(').trimEnd(')')
    }
}
