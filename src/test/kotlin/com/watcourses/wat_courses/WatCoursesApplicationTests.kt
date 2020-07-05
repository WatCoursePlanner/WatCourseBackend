package com.watcourses.wat_courses

import com.watcourses.wat_courses.persistence.DbCourse
import com.watcourses.wat_courses.persistence.DbCourseRepo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.*

@SpringBootTest
class WatCoursesApplicationTests {
    @Autowired
    lateinit var dbCourseRepo: DbCourseRepo

    @Test
    fun contextLoads() {
    }

    @Test
    fun `course info api returns correct data`() {

    }

    @Test
    fun `course db test`() {
        val course = DbCourse(name = "test", description = "", code = "", faculty = "", offeringTerm = null)
        dbCourseRepo.save(course)
        assertThat(dbCourseRepo.findAllById(listOf(course.id!!)).single()!!.name).isEqualTo("test")
    }
}
