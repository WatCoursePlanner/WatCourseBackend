package com.watcourses.wat_courses.api

import CourseInfo
import com.watcourses.wat_courses.persistence.DbCourseRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class CourseInfoApi {
    @Autowired
    private lateinit var dbCourseRepo: DbCourseRepo

    @GetMapping("/course/{code}")
    fun getCourse(@PathVariable code: String): CourseInfo {
        return dbCourseRepo.findByCode(code)?.toProto() ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Course with the code $code is not found"
        )
    }
}