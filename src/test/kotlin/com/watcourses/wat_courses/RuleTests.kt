package com.watcourses.wat_courses

import com.watcourses.wat_courses.rules.Condition
import com.watcourses.wat_courses.rules.Condition.Companion.alwaysFalse
import com.watcourses.wat_courses.rules.Condition.Companion.alwaysTrue
import com.watcourses.wat_courses.rules.Condition.Companion.and
import com.watcourses.wat_courses.rules.Condition.Companion.course
import com.watcourses.wat_courses.rules.Condition.Companion.not
import com.watcourses.wat_courses.rules.Condition.Companion.or
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RuleTests {
    @Test
    fun `checking works`() {
        val testSet = setOf("A", "B", "D")
        assertThat(alwaysTrue().check(emptySet())).isTrue()
        assertThat(alwaysFalse().check(emptySet())).isFalse()
        assertThat(course("A").check(testSet)).isTrue()
        assertThat(course("C").check(testSet)).isFalse()
        assertThat(and(course("A"), course("B")).check(testSet)).isTrue()
        assertThat(and(course("A"), course("B"), alwaysTrue()).check(testSet)).isTrue()
        assertThat(and(course("A"), course("B"), alwaysFalse()).check(testSet)).isFalse()
        assertThat(and(course("A"), course("C")).check(testSet)).isFalse()
        assertThat(or(course("A"), course("C")).check(testSet)).isTrue()
        assertThat(and(course("A"), not(course("C"))).check(testSet)).isTrue()
        assertThat(and(course("A"), not(course("A"))).check(testSet)).isFalse()
        assertThat(and(course("A"), not(and(course("A"), course("C")))).check(testSet)).isTrue()
        assertThat(and(course("A"), not(and(course("A"), course("D")))).check(testSet)).isFalse()
    }

    @Test
    fun `parsing works`() {
        val condition =
            Condition.parseFromText(
                "req: BME 121, CS 115, 135, 137, CHE 121, MTE 121/GENE 121, NE 111, MSCI 121"
            )
        assertThat(condition.toString())
            .isEqualTo("BME 121 && CS 115 && CS 135 && CS 137 && CHE 121 && (MTE 121 || GENE 121) && NE 111 && MSCI 121")
        assertThat(
            condition.check(
                setOf("BME 121", "CS 115", "CS 135", "CS 137", "CHE 121", "NE 111", "MSCI 121", "MTE 121", "EXTRA 100")
            )
        ).isTrue()
        assertThat(
            condition.check(
                setOf("BME 121", "CS 115", "CS 135", "CS 137", "CHE 121", "NE 111", "MSCI 121", "GENE 121", "EXTRA 100")
            )
        ).isTrue()
        assertThat(
            condition.check(
                setOf("BME 121", "CS 115", "CS 137", "CHE 121", "NE 111", "MSCI 121", "MTE 121", "EXTRA 100")
            )
        ).isFalse()
    }
}
