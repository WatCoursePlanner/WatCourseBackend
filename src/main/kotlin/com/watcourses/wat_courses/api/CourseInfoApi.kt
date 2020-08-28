package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.persistence.DbCourse
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.proto.*
import com.watcourses.wat_courses.rules.CourseListLoader
import com.watcourses.wat_courses.search.SearchManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
class CourseInfoApi(
    private val dbCourseRepo: DbCourseRepo,
    private val courseListLoader: CourseListLoader,
    private val searchManager: SearchManager
) {
    @GetMapping("/course/{code}")
    fun getCourse(@PathVariable code: String): CourseInfo {
        return dbCourseRepo.findByCode(code)?.toProto() ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Course with the code $code is not found"
        )
    }

    @PostMapping("/course/batch")
    fun batchGetCourse(@RequestBody request: BatchGetCourseRequest): BatchGetCourseResponse {
        return BatchGetCourseResponse(results = request.courseCodes.filter { it.isNotEmpty() }.map { getCourse(it) })
    }

    @PostMapping("/course/search")
    fun searchCourse(@RequestBody request: SearchCourseRequest): SearchCourseResponse {
        val (paginatedResults, paginationInfo) = searchManager.search(request)
        return SearchCourseResponse(
            pagination = paginationInfo,
            results = paginatedResults.map { DbCourse.toBasicInfoProto(it) }
        )
    }

    @GetMapping("/course_list/{name}")
    fun getCourseList(@PathVariable name: String): CourseList {
        return courseListLoader.getList(name)
    }
}
