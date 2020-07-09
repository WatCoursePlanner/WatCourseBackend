package com.watcourses.wat_courses

import com.watcourses.wat_courses.rules.Checker
import com.watcourses.wat_courses.rules.CourseListLoader
import com.watcourses.wat_courses.rules.DegreeRequirementLoader
import com.watcourses.wat_courses.rules.TermResolver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CheckerTests {
    @Autowired
    private lateinit var checker: Checker

    @Autowired
    private lateinit var courseListLoader: CourseListLoader

    @Autowired
    private lateinit var degreeRequirementLoader: DegreeRequirementLoader

    @Test
    fun `course list works`() {
        assertThat(courseListLoader.listContainsCourse("ATE_CS", "CS 123")).isTrue()
        assertThat(courseListLoader.listContainsCourse("ATE_CS", "DNE")).isFalse()
        assertThat(courseListLoader.listContainsCourse("ATE", "CS 123")).isTrue()
        assertThrows<Exception> { courseListLoader.listContainsCourse("DNE", "DNE") }
    }

    @Test
    fun `degree requirements loader works`() {
        val se = degreeRequirementLoader.getDegreeRequirement("Software Engineering")!!
        assertThat(se.source).contains("ugradcalendar")
        assertThat(se.labels).contains("Software Engineering", "Faculty of Mathematics")
        assertThat(se.defaultSchedule!!.terms.single { it.termName == "1A" }.courseCodes).contains("SE 101")
        assertThat(se.requirements.single { it.name == "Two Science Electives (SCE)" }.condition.toString())
            .isEqualTo("<SCE:2>")
    }

    @Test
    fun `getApplicableLabels works`() {
        assertThat(TermResolver.getApplicableLabels("1A"))
            .containsExactlyInAnyOrder("1A", "1st year")
        assertThat(TermResolver.getApplicableLabels("2B"))
            .containsExactlyInAnyOrder("1A", "1B", "2A", "2B", "1st year", "2nd year")
        assertThat(TermResolver.getApplicableLabels("4B"))
            .containsExactlyInAnyOrder(
                "1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B",
                "1st year", "2nd year", "3rd year", "4th year"
            )
    }
}