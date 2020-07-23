package com.watcourses.wat_courses

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbCourseScheduleRepo
import com.watcourses.wat_courses.proto.Term
import com.watcourses.wat_courses.scraping.ApiScheduleService
import com.watcourses.wat_courses.scraping.ScrapingCourseService
import com.watcourses.wat_courses.scraping.UwFlowScrapingService
import com.watcourses.wat_courses.utils.getCode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
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
    private lateinit var uwFlowScrapingService: UwFlowScrapingService

    @Autowired
    private lateinit var dbCourseRepo: DbCourseRepo

    @Autowired
    private lateinit var dbCourseScheduleRepo: DbCourseScheduleRepo

    @Autowired
    private lateinit var utils: Utils

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
        apiScheduleService.scrapeSchedulePage("ECE 140", "1205", inputStreamFromRes("schedule_ECE140.json"))
        apiScheduleService.scrapeSchedulePage("ECE 140", "1201", inputStreamFromRes("schedule_ECE140_1201.json"))

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

    @Test
    fun `uwflow scraping works`() {
        utils.createCourse("ECE 140", "ACTSC 391")
        val data = uwFlowScrapingService.parseData(inputStreamFromRes("uwflow.json"))
        assertThat(data).isNotNull
        val results = data.map { uwFlowScrapingService.persist(it) }
        assertThat(results.count { it }).isEqualTo(2)
        assertThat(results).hasSize(3)
        with(dbCourseRepo.findByCode("ECE 140")!!) {
            assertThat(liked).isCloseTo(0.80697, within(0.001))
            assertThat(easy).isCloseTo(0.61974, within(0.001))
            assertThat(useful).isCloseTo(0.84466, within(0.001))
            assertThat(filledCount).isEqualTo(373)
            assertThat(commentCount).isEqualTo(58)
        }
        with(dbCourseRepo.findByCode("ACTSC 391")!!) {
            assertThat(liked).isNull()
            assertThat(easy).isNull()
            assertThat(useful).isNull()
            assertThat(filledCount).isEqualTo(0)
            assertThat(commentCount).isEqualTo(0)
        }
    }
}
