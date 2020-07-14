package com.watcourses.wat_courses

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbCourseScheduleRepo
import com.watcourses.wat_courses.proto.Term
import com.watcourses.wat_courses.scraping.ScrapingCourseService
import com.watcourses.wat_courses.scraping.ScrapingScheduleService
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
    private lateinit var scrapingScheduleService: ScrapingScheduleService

    @Autowired
    private lateinit var dbCourseRepo: DbCourseRepo

    @Autowired
    private lateinit var dbCourseScheduleRepo: DbCourseScheduleRepo

    @Test
    fun `term id is correct`(){
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

    @Test
    fun `scraping schedule works`() {
        scrapingScheduleService.scrapeSchedulePage(
            "ECE 140", Jsoup.parse(ClassPathResource("schedule-ece140-1201.html").file, null)
        )
        scrapingScheduleService.scrapeSchedulePage(
            "ECE 140", Jsoup.parse(ClassPathResource("schedule-ece140-1205.html").file, null)
        )
        scrapingScheduleService.scrapeSchedulePage(
            "AE 221", Jsoup.parse(ClassPathResource("schedule-no-match.html").file, null)
        )

        dbCourseScheduleRepo.findByCodeAndTermId("ECE 140", "1201")!!.let { res ->
            assertThat(res.termId).isEqualTo("1201")
            with(res.sections.single { it.sectionId == 5090 }) {
                assertThat(section).isEqualTo("LEC 001")
                assertThat(enrolCap).isEqualTo(140)
                assertThat(enrolTotal).isEqualTo(156)
                assertThat(time).contains("02:30-03:20MWF", "03:30-04:20Th 01/23-01/23")
                assertThat(location).isEqualTo("UW U")
                assertThat(room).isEqualTo("E7 5343")
                assertThat(instructor).isEqualTo("Mohamed-Yahia Dabbagh")
                assertThat(reservedEnrolInfo.single().total).isEqualTo(28)
            }
            assertThat(res.enrolledCap).isEqualTo(281)
            assertThat(res.enrolledTotal).isEqualTo(283)
        }

        dbCourseScheduleRepo.findByCodeAndTermId("ECE 140", "1205")!!.let { res ->
            assertThat(res.termId).isEqualTo("1205")
            with(res.sections.single { it.sectionId == 3306 }) {
                assertThat(section).isEqualTo("LEC 002")
                assertThat(enrolCap).isEqualTo(150)
                assertThat(enrolTotal).isEqualTo(143)
                assertThat(time).containsExactly("TBA")
                assertThat(location).isEqualTo("ONLN ONLINE")
                assertThat(room).isEqualTo("")
                assertThat(instructor).isEqualTo("John Saad")
                assertThat(reservedEnrolInfo.single().total).isEqualTo(35)
            }
            assertThat(res.enrolledCap).isEqualTo(300)
            assertThat(res.enrolledTotal).isEqualTo(293)
        }

        dbCourseScheduleRepo.findByCodeAndTermId("AE 221", "1205")!!.let { res ->
            assertThat(res.sections).isEmpty()
            assertThat(res.enrolledCap).isEqualTo(0)
            assertThat(res.enrolledTotal).isEqualTo(0)
        }
    }
}
