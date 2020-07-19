package com.watcourses.wat_courses.rules

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.proto.ConditionType
import org.springframework.stereotype.Component

@Component
class RawConditionParser(private val dbCourseRepo: DbCourseRepo) {
    class ParseFailure(reason: String, val str: String? = null) : Exception(reason)

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
    // Make sure that each element of the parts is trimmed
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
    private fun parseFromCourseRequirementText(text: String): Condition {
        if (text.trim().startsWith("one of", ignoreCase = true)) {
            val parts =
                    text.trim().substring("one of".length).split(",", " or ", "/", ignoreCase = true)
                            .map { it.trim() }
            return Condition(ConditionType.OR, parts.mapIndexed { i, _ ->
                resolveCourse(parts, i)
            })
        }
        val andParts = text.split(",").map { it.trim() }.toMutableList()
        return Condition(ConditionType.AND, andParts.mapIndexed { i, part ->
            if (part.contains("/") || part.contains(" OR ", ignoreCase = true)) {
                val orParts = part.split("/", " OR ", ignoreCase = true)
                Condition(ConditionType.OR, orParts.mapIndexed { j, _ -> resolveCourse(orParts, j) })
            } else
                resolveCourse(andParts, i)
        })
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
    private fun tryParseLabelRequirements(text: String): Condition {
        var infoNotExtracted = text
        var cond = Condition(ConditionType.AND, listOf())
        val additionalMap = mapOf(
            "first year" to "1st year", "second year" to "2nd year",
            "third year" to "3rd year", "fourth year" to "4th year",
            "first-year" to "1st year", "second-year" to "2nd year",
            "third-year" to "3rd year", "fourth-year" to "4th year",
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
            "CPA" to "Mathematics/Chartered Professional Accountancy",
            "DAC" to "Digital Arts Communication",
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
            ",", ".", "/", "and", "Bachelor of", "majors", "not open to", "plan", "diploma",
            "students", "in", "only", "level", "least", "at", "least", "or", "of", "the"
        )

        for (ignoringWord in wordsToIgnore) {
            infoNotExtracted = infoNotExtracted.replace(ignoringWord, "", ignoreCase = true).trim()
        }

        if (infoNotExtracted.isNotBlank()) {
            throw ParseFailure("Has info not recognized: $infoNotExtracted")
        }

        if (foundList.isNotEmpty()) cond = cond.addOperand(Condition(ConditionType.OR, foundList.map { label(it) }))

        return when {
            text.contains("not open to", ignoreCase = true) -> not(cond) // revert conditions
            cond.operands.size == 1 -> cond.operands.single()
            cond.operands.isNotEmpty() -> cond
            else -> throw ParseFailure("Operands is empty. No info extracted") //null
        }
    }

    private fun safeParseCall(
        exceptionList: MutableList<Exception>,
        str: String,
        block: (String) -> Condition
    ): Condition? {
        return try {
            block(str)
        } catch (e: ParseFailure) {
            exceptionList.add(e)
            null
        }
    }

    private fun furtherTrim(text: String): String {  // test, temporary replacement for a self-check flag
        var trimmedStr = text
        val wordsToIgnore = listOf(  // strings that, when detected, raise flag
                "with a grade of", "at least", "60%", "65%", "70%", "75%", "80%",
                "1.0 unit", "0.50 unit", "0.5 unit", "(", ")", "taken", "prior to",
                "Portfolio Review Milestone", "4U", "Topic", "LEC"
        )
        for (ignoringWord in wordsToIgnore) {
            trimmedStr = trimmedStr.replace(ignoringWord, "", ignoreCase = true).trim()
        }

        return trimmedStr
    }

    fun parse(text: String): Condition {
        var processedText = text.substringAfter(":").trim()
        val conditions = mutableListOf<Condition>()
        for (partText in processedText.split(";").map { it.trim().trimEnd('.') }) {
            val clause = furtherTrim(partText)
            val exceptions = mutableListOf<Exception>()
            val parsedCondition = safeParseCall(exceptions, clause) { tryParseLabelRequirements(it) }
                ?: safeParseCall(exceptions, clause) { parseFromCourseRequirementText(it) }
                ?: throw ParseFailure(exceptions.joinToString("; ") { it.message.toString() }, text)
            conditions.add(parsedCondition)
        }
        if (conditions.size == 1) return conditions.single()
        return Condition(ConditionType.AND, conditions)
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
