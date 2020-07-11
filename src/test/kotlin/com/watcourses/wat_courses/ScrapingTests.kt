package com.watcourses.wat_courses

import com.watcourses.wat_courses.proto.Term
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.scraping.ScrapingService
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ScrapingTests {
    @Autowired
    private lateinit var scrapingService: ScrapingService

    @Autowired
    private lateinit var dbCourseRepo: DbCourseRepo

    @Test
    fun `scraping works`() {
        scrapingService.scrapeCoursePage(Jsoup.parse(ClassPathResource("course-CS.html").file, null))
            .forEach { scrapingService.persistCourse(it) }

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
}
