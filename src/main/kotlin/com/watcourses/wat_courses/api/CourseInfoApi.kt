package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.proto.*
import com.watcourses.wat_courses.rules.CourseListLoader
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
class CourseInfoApi(val dbCourseRepo: DbCourseRepo, val courseListLoader: CourseListLoader) {
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
    fun searchCourse(@RequestBody searchCourseRequest: SearchCourseRequest): SearchCourseResponse {
        val result = dbCourseRepo.findAll(
            PageRequest.of(
                searchCourseRequest.pagination?.zeroBasedPage ?: 0,
                searchCourseRequest.pagination?.limit ?: 30
            )
        )
        return SearchCourseResponse(
            pagination = PaginationInfoResponse(
                totalPages = result.totalPages,
                limit = result.size,
                currentPage = result.number
            ),
            results = result.content.map { it.toProto() }
        )
    }

    @GetMapping("/course_list/{name}")
    fun getCourseList(@PathVariable name: String): CourseList {
        return courseListLoader.getList(name)
    }
}