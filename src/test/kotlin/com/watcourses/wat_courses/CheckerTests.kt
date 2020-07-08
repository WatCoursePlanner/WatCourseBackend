package com.watcourses.wat_courses

import com.watcourses.wat_courses.rules.Checker
import com.watcourses.wat_courses.rules.ListResolver
import com.watcourses.wat_courses.rules.TermResolver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CheckerTests {
    @Autowired
    private lateinit var checker: Checker

    @Autowired
    private lateinit var listResolver: ListResolver

    @Test
    fun `checker works`() {
        listResolver.loadFiles()
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