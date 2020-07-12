package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.proto.StudentProfile
import com.watcourses.wat_courses.rules.DegreeRequirementLoader
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StudentProfileApi(private val degreeRequirementLoader: DegreeRequirementLoader) {
    @GetMapping("/profile/default")
    fun getDefaultStudentProfile(program: String): StudentProfile {
        val degreeRequirement = degreeRequirementLoader.getDegreeRequirement(program)!!
        return StudentProfile(
            schedule = degreeRequirement.defaultSchedule,
            degrees = listOf(program),
            labels = degreeRequirement.labels.toList()
        )
    }
}
