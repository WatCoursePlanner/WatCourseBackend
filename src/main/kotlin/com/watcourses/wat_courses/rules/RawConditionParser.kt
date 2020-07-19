package com.watcourses.wat_courses.rules

import com.watcourses.wat_courses.proto.ConditionType
import org.springframework.stereotype.Component

@Component
class RawConditionParser {
    class ParseFailure(reason: String) : Exception(reason)

    private fun courseSanityCheck(course: String) {
        val courseParts = course.split(" ")
        if (courseParts.size != 2
            || courseParts[0].length > 6
            || courseParts[0].any { it !in 'A'..'Z' }
            || courseParts[1].any { it !in '0'..'9' && it !in 'A'..'Z' }
            || courseParts[1].none { it in '0'..'9' } // must contain at least 1 number
        ) throw ParseFailure("sanity check failed: $course does not look like a course")
    }

    // resolve "123" in "CS 101, 123" to course("CS 123")
    // Make sure that each element of the parts is trimed
    private fun resolveCourse(parts: List<String>, index: Int): Condition {
        val part = parts[index].trim()
        if (part.contains(" ")) { // e.g. CS 101. We have "CS" already so return directly
            courseSanityCheck(part)
            return course(part)
        }
        // look backwards for identifier
        val identifier = parts.subList(0, index).findLast { it.contains(" ") }?.substringBefore(" ")
            ?: throw ParseFailure("Can't find a course identifier")
        val completeCourseCode = "$identifier $part".trim()
        courseSanityCheck(completeCourseCode)
        return course(completeCourseCode)
    }

    /*
     * Example: CS 101, 123, CS 102/ECE 123, CS 233 => AND(CS101, CS123, OR(CS102, ECE123), CS233)
     * One of CS 101, 123, or 233
     */
    private fun parseFromCourseRequirementText(text: String): Pair<Condition, Boolean> {
        if (text.trim().startsWith("one of", ignoreCase = true)) {
            val parts =
                text.trim().substring("one of".length).split(",", " or ", "/", ignoreCase = true)
                    .map { it.trim() }
            return Pair(Condition(ConditionType.OR, parts.mapIndexed { i, _ ->
                resolveCourse(parts, i)
            }), true)
        }
        val andParts = text.split(",").map { it.trim() }.toMutableList()
        return Pair(Condition(ConditionType.AND, andParts.mapIndexed { i, part ->
            if (part.contains("/") || part.contains(" OR ", ignoreCase = true)) {
                val orParts = part.split("/", " OR ", ignoreCase = true)
                Condition(ConditionType.OR, orParts.mapIndexed { j, _ -> resolveCourse(orParts, j) })
            } else
                resolveCourse(andParts, i)
        }), true)
    }

    /*
     * Parse major/level-related info. Examples:
     * level at (at least) xx (1A)
     * [not open to] xxx (major) students
     * [not open to] students in [1A/SE/1st year]
     * Fourth year Honours students in the Department of Recreation and Leisure Studies
     * A, B and C majors (major name can include "and" too)
     * Level at at least [1A] XXMajor
     *
     * Throws an exception when failed to parse
     */
    private fun tryParseLabelRequirements(text: String): Pair<Condition, Boolean> {
        var infoNotExtracted = text
        var cond = Condition(ConditionType.AND, listOf())
        val additionalMap = mapOf(
            "first year" to "1st year", "second year" to "2nd year",
            "third year" to "3rd year", "fourth year" to "4th year",
            "Year 1" to "1st year", "Year 2" to "2nd year",
            "Year 3" to "3rd year", "Year 4" to "4th year"
        )
        val possibleList = TermResolver.ALL_TERMS_TO_VALUES_MAP.keys.toSet() + additionalMap.keys
        val result = possibleList.find { text.contains(it, ignoreCase = true) }
        if (result != null) {
            cond = cond.addOperand(label(additionalMap[result] ?: result))
            infoNotExtracted = infoNotExtracted.replace(result, "")
        }

        val abbrMap = mapOf(
            "Eng" to "Engineering",
            "Fin" to "Financial",
            "Mgmt" to "Management",
            "Acc'ting" to "Accounting",
            "AHS" to "Applied Health Science",
            "Math" to "Mathematics",
            "&" to "and"
        )

        for (it in abbrMap) {
            infoNotExtracted = infoNotExtracted.replace(it.key + ",", it.value + ",")
                .replace(it.key + " ", it.value + " ")
        }

        val foundList = mutableListOf<String>()

        for (label in ALL_LABELS.sortedByDescending { it.length }) {
            if (infoNotExtracted.contains(label, ignoreCase = true)) {
                foundList.add(label)
                infoNotExtracted = infoNotExtracted.replace(label, "", ignoreCase = true)
            }
        }

        val wordsToIgnore = listOf(
            ",", ".", "/", "and", "Bachelor of", "majors", "not open to",
            "students", "in", "only", "level", "least", "at", "or", "of", "the"
        )

        for (ignoringWord in wordsToIgnore) {
            infoNotExtracted = infoNotExtracted.replace(ignoringWord, "", ignoreCase = true).trim()
        }

        if (infoNotExtracted.isNotBlank()) {
            throw ParseFailure("Has info not recognized: $infoNotExtracted")
        }

        if (foundList.isNotEmpty()) cond = cond.addOperand(Condition(ConditionType.OR, foundList.map { label(it) }))

        val retCondition = when {
            text.contains("not open to", ignoreCase = true) -> not(cond) // revert conditions
            cond.operands.size == 1 -> cond.operands.single()
            cond.operands.isNotEmpty() -> cond
            else -> alwaysTrue()
        }

        return Pair(retCondition, true)
    }

    /* Parser functions should return Pair<Condition, Boolean>,
     * in which Condition is the condition parsed,
     * and Boolean indicates if the condition is fully resolved
     * In case the parser does not understand the rule, it should
     * throw an exception, which will be converted to null in this
     * funciton.
    */
    private fun safeParseCall(
        exceptionList: MutableList<Exception>,
        str: String,
        block: (String) -> Pair<Condition, Boolean>
    ): Pair<Condition, Boolean>? {
        return try {
            block(str)
        } catch (e: ParseFailure) {
            exceptionList.add(e)
            null
        }
    }

    // returns a condition and whether it is fully understood
    // ParseFailure is thrown if parse failed.
    fun parse(text: String): Pair<Condition, Boolean> {
        var conditionFullyResolved = true
        val processedText = text.substringAfter(":").trim()
        val conditions = mutableListOf<Condition>()

        val clauses = processedText.split(";").map { it.trim().trimEnd('.') }.filterNot { it.isEmpty() }

        for (clause in clauses) {
            val exceptions = mutableListOf<Exception>()

            val (parsedCondition, fullyResolved) = safeParseCall(exceptions, clause) { tryParseLabelRequirements(it) }
                ?: safeParseCall(exceptions, clause) { parseFromCourseRequirementText(it) }
                ?: throw ParseFailure(exceptions.joinToString("; ") { it.message.toString() })

            conditionFullyResolved = conditionFullyResolved && fullyResolved
            conditions.add(parsedCondition)
        }

        if (conditions.size == 1) return Pair(conditions.single(), conditionFullyResolved)

        return Pair(Condition(ConditionType.AND, conditions), conditionFullyResolved)
    }

    companion object {
        fun and(vararg conditions: Condition): Condition {
            return if (conditions.size == 2 && conditions[0].type == ConditionType.AND)
                conditions[0].addOperand(conditions[1]) // flatten if possible
            else
                Condition(ConditionType.AND, conditions.toList())
        }

        fun or(vararg conditions: Condition): Condition {
            return if (conditions.size == 2 && conditions[0].type == ConditionType.OR)
                conditions[0].addOperand(conditions[1]) // flatten if possible
            else
                Condition(ConditionType.OR, conditions.toList())
        }

        fun not(condition: Condition) = Condition(ConditionType.NOT, listOf(condition))
        fun alwaysTrue() = Condition(ConditionType.TRUE, listOf())
        fun alwaysFalse() = Condition(ConditionType.FALSE, listOf())
        fun course(code: String) = Condition(ConditionType.HAS_COURSE, listOf(), data = code)
        fun label(label: String) = Condition(ConditionType.HAS_LABEL, listOf(), data = label)
        fun courseList(label: String, minimalCount: Int) = Condition(
            ConditionType.SATISFIES_LIST, listOf(), data = "$label:$minimalCount"
        )
    }
}
