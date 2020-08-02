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
    // Make sure that each element of the parts is trimmed
     private fun resolveCourse(parts: List<String>, index: Int): Condition {
        var part = parts[index].trim()
        if (part.contains(" ")) { // e.g. CS 101. We have "CS" already so return directly
            courseSanityCheck(part)
            return course(part)
        }
        // look backwards for identifier
        if ((part.all { it.isLetter() }) && (part.length > 1) && part!=parts.last()) { // contains a course code; look forward for course code
            val nextPart = parts[index + 1]
            if (nextPart.all { it.isLetter() }) throw ParseFailure("Expect $nextPart to have a course code")
            val courseCode = parts[index+1].substringAfter(" ")
            part = part + " " + courseCode.trim()
            courseSanityCheck(part)
            return course(part)
        }

        val prevParts = parts.subList(0, index)
        val identifier =prevParts.findLast { it.contains(" ") }?.substringBefore(" ")
                ?: throw ParseFailure("Can't find a course identifier")
        if (part.length == 1) { // e.g. ECE 123A/B
            var prevPart = parts[index - 1]
            if (!prevPart.last().isLetter()) throw ParseFailure("Expect $prevPart to have xxxA structure")
            if (!prevPart.contains(identifier)) prevPart = identifier + " " + prevPart.trim()
            prevPart = prevPart.trim().dropLast(1) + part
            courseSanityCheck(prevPart)
            return course(prevPart)
        }
        val completeCourseCode = "$identifier $part".trim()
        courseSanityCheck(completeCourseCode)
        return course(completeCourseCode)
    }

    // Find intervals (represented by offsets) that are inside brackets. (nested brackets not supported)
    private fun findBracketIntervals(text: String): List<Pair<Int, Int>> {
        val ret = mutableListOf<Pair<Int, Int>>()
        var startOffset = -1
        for ((ind, c) in text.withIndex()) {
            if (c == '(') {
                if (startOffset != -1) throw ParseFailure("Nested brackets not supported.")
                startOffset = ind
            } else if (c == ')') {
                if (startOffset == -1) throw ParseFailure("Brackets do not match")
                ret.add(Pair(startOffset, ind))
                startOffset = -1
            }
        }
        if (startOffset != -1) throw ParseFailure("Brackets do not match")
        return ret
    }

    private fun replaceInInterval(text: String, from: Int, to: Int, src: String, dest: String): String {
        return text.replaceRange(from, to, text.substring(from, to).replace(src, dest))
    }

    private fun resolvePart(parts: List<String>, index: Int): Condition {
        val part = parts[index].trim()
        if (part.contains("(")) {
            if (part.first() != '(' || part.last() != ')') throw ParseFailure("Brackets in unexpected positions")
            val ret = parseFromCourseRequirementText(part.substring(1, part.lastIndex))
            if (!ret.second) throw ParseFailure("resolvePart: Only support fully resolved condition")
            return ret.first
        }
        return resolveCourse(parts, index)
    }

    /*
     * Example: CS 101, 123, CS 102/ECE 123, CS 233 => AND(CS101, CS123, OR(CS102, ECE123), CS233)
     * One of CS 101, 123, or 233
     */
    // changed private fun -> fun for testing
    fun parseFromCourseRequirementText(text: String): Pair<Condition, Boolean> {
        var replacedText = text.replace(" OR ", "/", ignoreCase = true)
            .replace(" AND ", ",", ignoreCase = true)
            .replace(" & ", ",", ignoreCase = true)
            .replace("@", ",")
            .replace("#", "/")

        val bracketIntervals = findBracketIntervals(replacedText)

        for (interval in bracketIntervals) {
            // replace , to @ and / to # so they don't get split.
            // Do not change the length of the string. Otherwise the intervals would change.
            replacedText = replaceInInterval(replacedText, interval.first, interval.second, ",", "@")
            replacedText = replaceInInterval(replacedText, interval.first, interval.second, "/", "#")
        }

        if (replacedText.trim().startsWith("one of", ignoreCase = true)) {
            val parts =
                replacedText.trim().substring("one of".length).split(",", "/")
                    .map { it.trim() }
            return Pair(Condition(ConditionType.OR, parts.mapIndexed { i, _ ->
                resolvePart(parts, i)
            }), true)
        }
        val andParts = replacedText.split(",").map { it.trim() }.toMutableList()
        return Pair(Condition(ConditionType.AND, andParts.mapIndexed { i, part ->
            if (part.contains("/")) {
                val orParts = part.split("/")
                Condition(ConditionType.OR, orParts.mapIndexed { j, _ -> resolvePart(orParts, j) })
            } else
                resolvePart(andParts, i)
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
    fun tryParseLabelRequirements(text: String): Pair<Condition, Boolean> {
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
            infoNotExtracted = infoNotExtracted.replace(result, "", ignoreCase = true)
        }

        val abbrMap = mapOf(
            "Acc'ting" to "Accounting",
            "AHS" to "Applied Health Sciences",
            "Architectural" to "Architectural Engineering",
            "Architecture" to "Architectural Studies",
            "BA" to "Bachelor of Arts",
            "Biomedical" to "Biomedical Engineering",
            "Biotech/CPA" to "Biotechnology/Chartered Professional Accountancy",
            "Biotechnology/Chartered Accountancy" to "Biotechnology/Chartered Professional Accountancy",
            "Biotechnology/CPA" to "Biotechnology/Chartered Professional Accountancy",
            "BMath" to "Bachelor of Mathematics",
            "BSc" to "Bachelor of Science",
            "Civil" to "Civil Engineering",
            "Chemical" to "Chemical Engineering",
            "Comp & Financial" to "Computing & Financial",
            "Comp or Elect" to "Computer Engineering or Electric",
            "Computer," to "Computer Engineering",
            "Coop" to "Co-op",
            "Department of Recreation and Leisure Studies" to "Recreation and Leisure Studies",
            "Digital Hdw Op" to "Digital Hardware Option",
            "Econ" to "Economics",
            "Economic" to "Economics",
            "Electrical" to "Electrical Engineering",
            "Eng" to "Engineering",
            "Environment" to "Faculty of Environment",
            "Environment, Resource and Sustainability" to "Environment, Resources and Sustainability",
            "Environmental," to "Environmental Engineering",
            "Fin" to "Financial",
            "Geological" to "Geological Engineering",
            "Global Business," to "Global Business and Digital Arts",
            "Global Business students" to "Global Business and Digital Arts",
            "GSJ" to "Gender and Social Justice",
            "Hon" to "Honours",
            "HRM" to "Human Resources Management",
            "Management," to "Management Engineering",
            "Math" to "Mathematics",
            "Math/Accounting" to "Mathematics/Chartered Professional Accountancy",
            "Math/Chartered Professional Accountancy" to "Mathematics/Chartered Professional Accountancy",
            "Math/CPA" to "Mathematics/Chartered Professional Accountancy",
            "Math/FARM" to "Mathematics/Financial Analysis and Risk Management",
            "Math/Financial Analysis and Risk Management" to "Mathematics/Financial Analysis and Risk Management",
            "Math/ITM" to "Mathematics/Information Technology Management",
            "Math/Phys" to "Mathematical Physics",
            "Mathematics/Chartered Accountancy" to "Mathematics/Chartered Professional Accountancy",
            "Mathematics Chartered Accountancy" to "Mathematics/Chartered Professional Accountancy",
            "Mathematics/Chartered Professional Accounting" to "Mathematics/Chartered Professional Accountancy",
            "Mathematics/CPA" to "Mathematics/Chartered Professional Accountancy",
            "Mechanical" to "Mechanical Engineering",
            "Mechatronics" to "Mechatronics Engineering",
            "Mgmt" to "Management Engineering",
            "MSCI" to "Management Sciences",
            "Nanotechnology" to "Nanotechnology Engineering",
            "Option in Aging Studies" to "Aging Studies Option",
            "PHARM" to "Pharmacy",
            "Psych" to "Psychology",
            "Rec & Business" to "Recreation and Business",
            "Rec & Leisure Studies" to "Recreation and Leisure Studies",
            "Recreation and Leisure Students" to "Recreation and Leisure Studies",
            "RI Spec" to "Research Intensive Specialization",
            "SCI" to "Science",
            "Social Policy students" to "Social Policy Specialization",
            "Software" to "Software Engineering",
            "stdnts" to "students",
            "Systems Design," to "Systems Design Engineering",
            "Systems Designs Engineering" to "Systems Design Engineering",
            "&" to "and"
        )

        for (it in abbrMap) {
            infoNotExtracted = infoNotExtracted.replace(it.key + ",", it.value + ",", ignoreCase = true)
                .replace(it.key + " ", it.value + " ", ignoreCase = true)
            println(infoNotExtracted + "    " + it)
        }

        infoNotExtracted = infoNotExtracted.replace("Engineering Engineering", "Engineering")

        val foundList = mutableListOf<String>()

        for (label in ALL_LABELS.sortedByDescending { it.length }) {
            if (infoNotExtracted.contains(label, ignoreCase = true)) {
                foundList.add(label)
                infoNotExtracted = infoNotExtracted.replace(label, "", ignoreCase = true)
            }
        }

        val wordsToIgnore = listOf(
            ",", ".", "/", "and", "Bachelor of", "majors", "not open to", "open to", "open only to",
            "following faculties:", "students", "in", "only", "level", "least", "at", "or", "of", "the",
            "diploma", "plans", "for", "programs", "faculty"
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

    // Remove stuff we can't recognize
    fun preprocess(text: String): String {
        val patterns = listOf(
            "\\(LEC \\d+\\)", // section requirement
            "(with a grade of|with a cumulative average of|Cumulative overall average of|with a major average of|average|cumulative major average)\\s??[Aa]t least \\d+%( in)?", // grade req
            "(taken (in or before )?|prior to)(\\s?(or\\s?)?((spring|fall|winter) 20\\d\\d|[SWF]\\d\\d),?\\s?)+", // time req
            "\\(\\s*\\)" // extra empty parenthesis
        )
        var str = text
        patterns.forEach { str = str.replace(Regex(it), "") }
        return str
    }

    // returns a condition and whether it is fully understood
    // ParseFailure is thrown if parse failed.
    fun parse(text: String): Pair<Condition, Boolean> {
        var conditionFullyResolved = true
        val trimmedText = text.substringAfter(":").trim()
        val processedText = preprocess(trimmedText)
        if (trimmedText != processedText) conditionFullyResolved = false

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
