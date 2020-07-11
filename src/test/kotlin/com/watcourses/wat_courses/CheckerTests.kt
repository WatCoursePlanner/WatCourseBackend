package com.watcourses.wat_courses

import com.watcourses.wat_courses.persistence.DbCourse
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.proto.Schedule
import com.watcourses.wat_courses.proto.StudentProfile
import com.watcourses.wat_courses.rules.Checker
import com.watcourses.wat_courses.rules.CourseListLoader
import com.watcourses.wat_courses.rules.DegreeRequirementLoader
import com.watcourses.wat_courses.rules.TermResolver
import com.watcourses.wat_courses.utils.ClassPathResourceReader
import com.watcourses.wat_courses.utils.build
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class CheckerTests {
    @Autowired
    private lateinit var checker: Checker

    @Autowired
    private lateinit var courseListLoader: CourseListLoader

    @Autowired
    private lateinit var degreeRequirementLoader: DegreeRequirementLoader

    @Autowired
    private lateinit var dbCourseRepo: DbCourseRepo

    @MockBean
    private lateinit var resourceReader: ClassPathResourceReader

    @BeforeEach
    fun setup() {
        given(resourceReader.get("degrees")).willReturn(ClassPathResource("degrees"))
        given(resourceReader.get("lists")).willReturn(ClassPathResource("lists"))

        dbCourseRepo.deleteAll()
        createCourse("CS 442", "AE 101", "ANTH 100", "ECON 221", "ECON 101", "PSYCH 420", "ECE 409")

        courseListLoader.loadLists()
        degreeRequirementLoader.loadDegreeRequirements()
    }

    @Test
    fun `course list works`() {
        // Simple list tests
        assertThat(courseListLoader.listContainsCourse("ATE_CS", "CS 442")).isTrue()
        assertThat(courseListLoader.listContainsCourse("ATE_CS", "DNE")).isFalse()

        // Test list with includes
        assertThat(courseListLoader.listContainsCourse("ATE", "CS 442")).isTrue()
        assertThat(courseListLoader.listContainsCourse("ATE", "ECE 409")).isTrue()

        // Test non-existing list
        assertThrows<Exception> { courseListLoader.listContainsCourse("DNE", "DNE") }

        // Test lists with wildcards & except & wildcard except
        assertThat(courseListLoader.listContainsCourse("Humanities_and_Social_Sciences", "AE 101")).isTrue()
        assertThat(courseListLoader.listContainsCourse("Humanities_and_Social_Sciences", "ANTH 100")).isTrue()
        assertThat(courseListLoader.listContainsCourse("Humanities_and_Social_Sciences", "ECON 221")).isFalse()
        assertThat(courseListLoader.listContainsCourse("Humanities_and_Social_Sciences", "ECON 101")).isTrue()
        assertThat(courseListLoader.listContainsCourse("Humanities_and_Social_Sciences", "PSYCH 420")).isFalse()

    }

    @Test
    fun `degree requirements loader works`() {
        val se = degreeRequirementLoader.getDegreeRequirement("Software Engineering")!!
        assertThat(se.source).contains("ugradcalendar")
        assertThat(se.labels).contains("Software Engineering", "Faculty of Mathematics")
        assertThat(se.defaultSchedule!!.terms.single { it.termName == "1A" }.courseCodes).contains("SE 101")
        assertThat(se.requirements.single { it.name == "Three Advanced Technical Electives (ATE)" }.condition.toString())
            .isEqualTo("<ATE_CS:1> && <ATE_ECE:1> && <ATE:3>")
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

    @Test
    fun `checker works`() {
        val result = checker.check(
            StudentProfile(
                schedule = Schedule.build(mapOf("1A" to listOf("CS 442"))),
                degrees = listOf("Software Engineering")
            )
        )
        assertThat(result.issues).isNotEmpty()
    }

    private fun createCourse(vararg code: String) =
        code.forEach {
            dbCourseRepo.save(
                DbCourse(
                    name = "$it name",
                    code = it,
                    antiRequisite = null,
                    coRequisite = null,
                    description = "",
                    offeringTerms = null,
                    preRequisite = null,
                    courseId = it
                )
            )
        }
}
