package com.watcourses.wat_courses

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbCourseScheduleRepo
import com.watcourses.wat_courses.proto.Term
import com.watcourses.wat_courses.scraping.ApiScheduleService
import com.watcourses.wat_courses.scraping.ScrapingCourseService
import com.watcourses.wat_courses.utils.getCode
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ScrapingTests {
    @Autowired
    private lateinit var scrapingCourseService: ScrapingCourseService

    @Autowired
    private lateinit var apiScheduleService: ApiScheduleService

    @Autowired
    private lateinit var dbCourseRepo: DbCourseRepo

    @Autowired
    private lateinit var dbCourseScheduleRepo: DbCourseScheduleRepo

    @Test
    fun `term id is correct`() {
        assertThat(Term.FALL.getCode(2019)).isEqualTo(1199)
        assertThat(Term.SPRING.getCode(2020)).isEqualTo(1201)
        assertThat(Term.WINTER.getCode(2020)).isEqualTo(1205)
        assertThat(Term.FALL.getCode(2020)).isEqualTo(1209)
        assertThat(Term.SPRING.getCode(2021)).isEqualTo(1211)
    }

    @Test
    fun `scraping works`() {
        scrapingCourseService.scrapeCoursePage(Jsoup.parse(ClassPathResource("course-CS.html").file, null))
            .forEach { scrapingCourseService.persistCourse(it) }

        val courses = dbCourseRepo.findAll().filterNotNull()
        with(courses.single { it.code == "CS 100" }) {
            assertThat(name).isEqualTo("Introduction to Computing through Applications")
            assertThat(offeringTerms).containsExactlyInAnyOrder(Term.FALL, Term.WINTER, Term.SPRING)
            assertThat(antiRequisite!!.rawRule).startsWith("Antireq: All second,third or fourth year CS")
            assertThat(preRequisite!!.rawRule).startsWith("Prereq: Not open to Mathematics")
            assertThat(courseId).isEqualTo("004360")
            assertThat(description).startsWith("Using personal computers")
        }

        assertThat(dbCourseRepo.findByCode("CS 492")!!.name).isEqualTo("The Social Implications of Computing")
    }

    private fun inputStreamFromRes(file: String) = ClassPathResource(file).inputStream

    @Test
    fun `api schedule info extraction works`() {
        apiScheduleService.scrapeSchedulePage("ECE 140", "1205", inputStreamFromRes("schedule_ece140.json"))
        apiScheduleService.scrapeSchedulePage("ECE 140", "1201", inputStreamFromRes("schedule_ece140_1201.json"))

        dbCourseScheduleRepo.findByCodeAndTermId("ECE 140", "1201")!!.let { res ->
            assertThat(res.termId).isEqualTo("1201")
            with(res.sections.single { it.classNumber == 5090 }) {
                assertThat(section).isEqualTo("LEC 001")
                assertThat(enrollmentCapacity).isEqualTo(140)
                assertThat(enrollmentTotal).isEqualTo(156)
                assertThat(reserves.single().enrollmentTotal).isEqualTo(28)
            }
            assertThat(res.enrolledCap).isEqualTo(281)
            assertThat(res.enrolledTotal).isEqualTo(283)
        }

        dbCourseScheduleRepo.findByCodeAndTermId("ECE 140", "1205")!!.let { res ->
            assertThat(res.termId).isEqualTo("1205")
            with(res.sections.single { it.classNumber == 3306 }) {
                assertThat(section).isEqualTo("LEC 002")
                assertThat(enrollmentCapacity).isEqualTo(150)
                assertThat(enrollmentTotal).isEqualTo(143)
                assertThat(reserves.single().enrollmentTotal).isEqualTo(35)
            }
            assertThat(res.enrolledCap).isEqualTo(300)
            assertThat(res.enrolledTotal).isEqualTo(295)
        }
    }
}
