package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.Utils
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbStudentProfileRepo
import com.watcourses.wat_courses.proto.CoopStream
import com.watcourses.wat_courses.proto.CreateStudentProfileRequest
import com.watcourses.wat_courses.proto.Schedule
import com.watcourses.wat_courses.proto.StudentProfile
import com.watcourses.wat_courses.proto.Term
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import javax.transaction.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class StudentProfileApiTests {
    @Autowired
    private lateinit var studentProfileApi: StudentProfileApi

    @Autowired
    private lateinit var dbStudentProfileRepo: DbStudentProfileRepo

    @Autowired
    private lateinit var utils: Utils

    @Test
    fun `create student profile`() {
        utils.createCourse(*SAMPLE_PROFILE_COURSES.toTypedArray())
        val studentProfile = studentProfileApi.createStudentProfile(
            CreateStudentProfileRequest(
                degrees = listOf("Software Engineering"),
                startingYear = 2019,
                coopStream = CoopStream.STREAM_8,
            )
        )
        assertThat(studentProfile).isEqualTo(SAMPLE_PROFILE)
        assertThat(dbStudentProfileRepo.findAll().single()!!.toProto()).isEqualTo(SAMPLE_PROFILE)
    }

    companion object {
        private val SAMPLE_PROFILE = StudentProfile(
            schedule = Schedule(
                terms = listOf(
                    Schedule.TermSchedule(
                        courseCodes = listOf("CS 137", "ECE 105", "MATH 115", "MATH 117", "MATH 135", "SE 101"),
                        termName = "1A", year = 2019, term = Term.FALL
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 102", "CS 138", "ECE 106", "ECE 124", "ECE 140", "MATH 119"),
                        termName = "1B", year = 2020, term = Term.WINTER
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 201", "CHE 102", "CS 241", "ECE 222", "SE 212", "STAT 206"),
                        termName = "2A", year = 2020, term = Term.SPRING
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 202", "CS 240", "CS 247", "CS 348", "ECE 192", "MATH 239", "WKRPT 200"),
                        termName = "2B", year = 2020, term = Term.FALL
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 301", "CS 341", "CS 349", "MATH 213", "SE 350", "SE 465"),
                        termName = "3A", year = 2021, term = Term.WINTER
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 302", "CS 343", "ECE 358", "SE 380", "SE 390", "SE 464", "WKRPT 300"),
                        termName = "3B", year = 2021, term = Term.SPRING
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 401", "SE 463", "SE 490", "WKRPT 400"),
                        termName = "4A", year = 2021, term = Term.FALL
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 402", "SE 491"),
                        termName = "4B", year = 2022, term = Term.WINTER
                    ),
                ),
            ),
            labels = listOf("Software Engineering", "Engineering", "Faculty of Mathematics"),
            degrees = listOf("Software Engineering"),
            shortList = listOf(),
            ownerEmail = null,
        )
        private val SAMPLE_PROFILE_COURSES = SAMPLE_PROFILE.schedule!!.terms.flatMap { it.courseCodes }
    }
}