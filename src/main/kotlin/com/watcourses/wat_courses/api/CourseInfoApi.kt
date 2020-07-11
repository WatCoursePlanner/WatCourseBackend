package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.proto.CourseInfo
import com.watcourses.wat_courses.proto.CourseList
import com.watcourses.wat_courses.rules.CourseListLoader
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class CourseInfoApi(val dbCourseRepo: DbCourseRepo, val courseListLoader: CourseListLoader) {
    @GetMapping("/course/{code}")
    fun getCourse(@PathVariable code: String): CourseInfo {
        return dbCourseRepo.findByCode(code)?.toProto() ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Course with the code $code is not found"
        )
    }

    @GetMapping("/course_list/{name}")
    fun getCourseList(@PathVariable name: String): CourseList {
        return courseListLoader.getList(name)
    }
}