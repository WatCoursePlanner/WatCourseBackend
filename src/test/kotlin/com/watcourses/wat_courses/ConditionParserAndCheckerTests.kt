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

@SpringBootTest
class ConditionParserAndCheckerTests {
    @Autowired
    private lateinit var checker: Checker

    @Autowired
    private lateinit var rawConditionParser: RawConditionParser

    fun Condition.check(state: StudentState) = checker.checkCondition(this, state)
    fun Condition.Companion.parse(text: String) = rawConditionParser.parse(text)

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

        assertThat(Condition.parse("Level at least 4B Mechanical Engineering students only.").toString())
            .isEqualTo("[4B] && [Mechanical Engineering]")
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
    fun `get related courses works`() {
        val cond = ConditionParser.parseToEnd("!(MATH 233 || (<list1:1>&&true)) && <list2:2> or <list3:3>")
        assertThat(cond.getRelatedCourses()).containsExactlyInAnyOrder("MATH 233")
        assertThat(cond.getRelatedCourseLists()).containsExactlyInAnyOrder("list1", "list2", "list3")
    }
}
