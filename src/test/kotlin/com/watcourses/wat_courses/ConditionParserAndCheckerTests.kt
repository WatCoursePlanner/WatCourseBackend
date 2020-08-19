package com.watcourses.wat_courses

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.watcourses.wat_courses.rules.*
import com.watcourses.wat_courses.rules.RawConditionParser.Companion.alwaysFalse
import com.watcourses.wat_courses.rules.RawConditionParser.Companion.alwaysTrue
import com.watcourses.wat_courses.rules.RawConditionParser.Companion.and
import com.watcourses.wat_courses.rules.RawConditionParser.Companion.course
import com.watcourses.wat_courses.rules.RawConditionParser.Companion.not
import com.watcourses.wat_courses.rules.RawConditionParser.Companion.or
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ConditionParserAndCheckerTests {
    @Autowired
    private lateinit var checker: Checker

    @Autowired
    private lateinit var rawConditionParser: RawConditionParser

    fun Condition.check(state: StudentState) = checker.checkCondition(this, state)
    fun Condition.Companion.parse(text: String) = rawConditionParser.parse(text).first
    fun Condition.Companion.parseFullInfo(text: String) = rawConditionParser.parse(text)

    @Test
    fun `checking works`() {
        val testState = StudentState(setOf("A", "B", "D"), emptySet())
        assertThat(alwaysTrue().check(testState)).isTrue()
        assertThat(alwaysFalse().check(testState)).isFalse()
        assertThat(course("A").check(testState)).isTrue()
        assertThat(course("C").check(testState)).isFalse()
        assertThat(and(course("A"), course("B")).check(testState)).isTrue()
        assertThat(and(course("A"), course("B"), alwaysTrue()).check(testState)).isTrue()
        assertThat(and(course("A"), course("B"), alwaysFalse()).check(testState)).isFalse()
        assertThat(and(course("A"), course("C")).check(testState)).isFalse()
        assertThat(or(course("A"), course("C")).check(testState)).isTrue()
        assertThat(and(course("A"), not(course("C"))).check(testState)).isTrue()
        assertThat(and(course("A"), not(course("A"))).check(testState)).isFalse()
        assertThat(and(course("A"), not(and(course("A"), course("C")))).check(testState)).isTrue()
        assertThat(and(course("A"), not(and(course("A"), course("D")))).check(testState)).isFalse()
    }

    @Test
    fun `parsing raw requirements works`() {
        data class TestCase(
            val rawRule: String,
            val expectedCond: String,
            val expectedConditionFullyResolved: Boolean = true
        )

        val testCases = listOf(
            TestCase("BIOL 123A/B", "(BIOL 123A || BIOL 123B)"),
            TestCase(
                rawRule = "Prereq: Civil Engineering.",
                expectedCond = "[Civil Engineering]"
            ),
            TestCase(
                rawRule = "Antireq: AFM 415 (LEC 001) taken fall 2017, fall 2018",
                expectedCond = "AFM 415",
                expectedConditionFullyResolved = false
            ),
            TestCase(
                rawRule = "AFM 417 taken S14, S15, S16, W16",
                expectedCond = "AFM 417",
                expectedConditionFullyResolved = false
            ),
            TestCase(
                rawRule = "Prereq: (MATH 225 or 235 or 245) and (STAT 221 or 231 or 241).",
                expectedCond = "((MATH 225 || MATH 235 || MATH 245)) && ((STAT 221 || STAT 231 || STAT 241))"
            ),
            TestCase(
                rawRule = "Prereq: (CS 245 or SE 212), (one of CS 241, 246, 247), (one of STAT 206, 230, 240);",
                expectedCond = "((CS 245 || SE 212)) && (CS 241 || CS 246 || CS 247) && (STAT 206 || STAT 230 || STAT 240)"
            ),
            TestCase(
                rawRule = "Prereq: Level at least 3A; One of HUMSC 101, 102, 201, 301",
                expectedCond = "[3A] && (HUMSC 101 || HUMSC 102 || HUMSC 201 || HUMSC 301)"
            ),
            TestCase(
                rawRule = "Prereq: One of GSJ 101, 102, WS 101, 102",
                expectedCond = "GSJ 101 || GSJ 102 || WS 101 || WS 102"
            ),
            TestCase(
                rawRule = "req: One of SPAN 352W, 362W, 401/402",
                expectedCond = "SPAN 352W || SPAN 362W || SPAN 401 || SPAN 402"
            ),
            TestCase(
                rawRule = "Level at least 4B Mechanical Engineering students only.",
                expectedCond = "[4B] && [Mechanical Engineering]"
            ),
            TestCase(
                rawRule = "Level at least 4B; MCS 138",
                expectedCond = "[4B] && MCS 138"
            )
        )

        for (case in testCases) {
            println("Testing $case")
            assertThat(Condition.parseFullInfo(case.rawRule).toString()).isEqualTo(
                Pair(case.expectedCond, case.expectedConditionFullyResolved).toString()
            )
        }

        Condition.parse(
            "req: BME 121, CS 115, 135, 137, CHE 121, MTE 121/GENE 121, NE 111, MSCI 121"
        ).let {
            assertThat(it.toString())
                .isEqualTo("BME 121 && CS 115 && CS 135 && CS 137 && CHE 121 && (MTE 121 || GENE 121) && NE 111 && MSCI 121")
            assertThat(
                it.check(
                    StudentState(
                        setOf(
                            "BME 121", "CS 115", "CS 135", "CS 137", "CHE 121", "NE 111", "MSCI 121", "MTE 121",
                            "EXTRA 100"
                        ), emptySet()
                    )
                )
            ).isTrue()
            assertThat(
                it.check(
                    StudentState(
                        setOf(
                            "BME 121", "CS 115", "CS 135", "CS 137", "CHE 121", "NE 111", "MSCI 121", "GENE 121",
                            "EXTRA 100"
                        ), emptySet()
                    )
                )
            ).isTrue()
            assertThat(
                it.check(
                    StudentState(
                        setOf(
                            "BME 121", "CS 115", "CS 137", "CHE 121", "NE 111", "MSCI 121", "MTE 121", "EXTRA 100"
                        ), emptySet()
                    )
                )
            ).isFalse()
        }

        Condition.parse(
            "CS 241; Software Engineering students only."
        ).let {
            assertThat(it.toString()).isEqualTo("CS 241 && [Software Engineering]")
            assertThat(it.check(StudentState(setOf("CS 241"), setOf("Software Engineering")))).isTrue()
            assertThat(it.check(StudentState(setOf(), setOf("Software Engineering")))).isFalse()
            assertThat(it.check(StudentState(setOf("CS 241"), setOf()))).isFalse()
        }

        Condition.parse(
            "One of CS 116, 136, 138, 146; Not open to Computer Science students."
        ).let {
            assertThat(it.toString()).isEqualTo("(CS 116 || CS 136 || CS 138 || CS 146) && ![Computer Science]")
            assertThat(it.check(StudentState(setOf("CS 116"), setOf("Software Engineering")))).isTrue()
            assertThat(it.check(StudentState(setOf("CS 123"), setOf("Software Engineering")))).isFalse()
            assertThat(it.check(StudentState(setOf("CS 116"), setOf("Computer Science")))).isFalse()
        }
    }

    @Test
    fun `condition string parser works`() {
        assertThat(
            ConditionParser.parseToEnd("CS 123 && !(MATH 233 || [label2]) && [label] or CS 101 and [1A]").toString()
        ).isEqualTo("(CS 123 && !(MATH 233 || [label2]) && [label]) || (CS 101 && [1A])")
        assertThat(
            ConditionParser.parseToEnd("!CS 123 && <list:2> || <ATE_CS:3>").toString()
        ).isEqualTo("(!CS 123 && <list:2>) || <ATE_CS:3>")
    }

    @Test
    fun `condition minify`() {
        assertThat(
            ConditionParser.parseToEnd(
                "CS 123 && [1A] && ([2A]) && (CS 233 || (ECE 123 || (MATH 123)))"
            ).minify().toString()
        ).isEqualTo("CS 123 && [1A] && [2A] && (CS 233 || ECE 123 || MATH 123)")

        assertThat(
            ConditionParser.parseToEnd(
                "(CS 233 || (ECE 123 || (MATH 123 && CS 123 && ([1A] || [1B]))))"
            ).minify().toString()
        ).isEqualTo("CS 233 || ECE 123 || (MATH 123 && CS 123 && ([1A] || [1B]))")
    }

    @Test
    fun `get related courses works`() {
        val cond = ConditionParser.parseToEnd("!(MATH 233 || (<list1:1>&&true)) && <list2:2> or <list3:3>")
        assertThat(cond.getRelatedCourses()).containsExactlyInAnyOrder("MATH 233")
        assertThat(cond.getRelatedCourseLists()).containsExactlyInAnyOrder("list1", "list2", "list3")
    }
}

