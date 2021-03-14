package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.Utils
import com.watcourses.wat_courses.persistence.DbStudentProfileRepo
import com.watcourses.wat_courses.persistence.DbUserRepo
import com.watcourses.wat_courses.proto.CoopStream
import com.watcourses.wat_courses.proto.CreateDefaultStudentProfileRequest
import com.watcourses.wat_courses.proto.CreateStudentProfileRequest
import com.watcourses.wat_courses.proto.Schedule
import com.watcourses.wat_courses.proto.StudentProfile
import com.watcourses.wat_courses.proto.Term
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
    private lateinit var dbUserRepo: DbUserRepo

    @Autowired
    private lateinit var utils: Utils

    @Test
    fun `create student profile`() {
        utils.createCourses(SAMPLE_PROFILE.allCourseCodes())
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

    @Test
    fun `create default student profile`() {
        utils.createCourses(SAMPLE_PROFILE.allCourseCodes())
        val ownerEmail = "a@b.com"
        utils.createUserWithEmail(ownerEmail)
        val profileWithOwnerEmail = SAMPLE_PROFILE.copy(ownerEmail = ownerEmail)
        val studentProfile = studentProfileApi.createDefaultStudentProfile(
            CreateDefaultStudentProfileRequest(
                degrees = listOf("Software Engineering"),
                startingYear = 2019,
                coopStream = CoopStream.STREAM_8,
                ownerEmail = ownerEmail,
            )
        )
        assertThat(studentProfile).isEqualTo(profileWithOwnerEmail)
        assertThat(dbStudentProfileRepo.findAll().single()!!.toProto()).isEqualTo(profileWithOwnerEmail)
        assertThat(dbUserRepo.findByEmail(ownerEmail)!!.studentProfile!!.toProto()).isEqualTo(profileWithOwnerEmail)
    }

    @Test
    fun `create or update student profile`() {
        utils.createCourses(SAMPLE_PROFILE.allCourseCodes())
        assertThatThrownBy { studentProfileApi.createOrUpdateStudentProfile(SAMPLE_PROFILE) }
            .hasMessageContaining("No owner provided")

        val ownerEmail = "a@b.com"
        val profileWithOwnerEmail = SAMPLE_PROFILE.copy(ownerEmail = ownerEmail)
        assertThatThrownBy { studentProfileApi.createOrUpdateStudentProfile(profileWithOwnerEmail) }
            .hasMessageContaining("User with email $ownerEmail is not found")

        utils.createUserWithEmail(ownerEmail)
        var studentProfile = studentProfileApi.createOrUpdateStudentProfile(profileWithOwnerEmail)
        assertThat(studentProfile).isEqualTo(profileWithOwnerEmail)
        assertThat(dbStudentProfileRepo.findAll().single()!!.toProto()).isEqualTo(profileWithOwnerEmail)
        assertThat(dbUserRepo.findByEmail(ownerEmail)!!.studentProfile!!.toProto()).isEqualTo(profileWithOwnerEmail)

        val updatedProfile = studentProfile.copy(
            schedule = studentProfile.schedule!!.copy(
                terms = listOf(
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 789", "SE 491"),
                        termName = "5B", year = 2077, term = Term.WINTER
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 302", "CS 343", "ECE 358", "SE 380", "SE 390", "SE 464", "WKRPT 300"),
                        termName = "3B", year = 6767, term = Term.SPRING
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("CS 137", "ECE 222", "SE 101", "MATH 135"),
                        termName = "1A", year = 4545, term = Term.SPRING
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 402", "SE 491"),
                        termName = "4B", year = 1998, term = Term.WINTER
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 402", "SE 491"),
                        termName = "5B", year = 5839, term = Term.FALL
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 201",  "CS 241", "ECE 222", "CHE 102", "SE 999", "STAT 206"),
                        termName = "2A", year = 2333, term = Term.FALL
                    ),
                    Schedule.TermSchedule(
                        courseCodes = listOf("SE 233", "SE 112"),
                        termName = "6B", year = 2022, term = Term.WINTER
                    ),
                )
            ),
            labels = listOf("Faculty of Bugs", "Bug Engineering", "Engineering"),
            degrees = listOf("Bug Engineering"),
            shortList = listOf("CS 2077", "SCI 238"),
        )
        utils.createCourses(updatedProfile.allCourseCodes())
        studentProfile = studentProfileApi.createOrUpdateStudentProfile(updatedProfile)
        assertThat(studentProfile).isEqualTo(updatedProfile)
        assertThat(dbStudentProfileRepo.findAll().single()!!.toProto()).isEqualTo(updatedProfile)
        assertThat(dbUserRepo.findByEmail(ownerEmail)!!.studentProfile!!.toProto()).isEqualTo(updatedProfile)
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
        private fun StudentProfile.allCourseCodes() = schedule!!.terms.flatMap { it.courseCodes }.plus(shortList)
    }
}