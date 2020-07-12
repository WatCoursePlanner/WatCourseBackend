package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.proto.*
import com.watcourses.wat_courses.utils.build
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleStudentProfileApi {
    @GetMapping("/sample_student_profile")
    fun getSampleStudentProfile(): StudentProfile {
        return StudentProfile(
                schedule = Schedule.build(mapOf("1A" to listOf("CS 442"))),
                degrees = listOf("Software Engineering")
        )
    }
}