package com.watcourses.wat_courses.api

import CourseInfo
import Term
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CourseInfoApi {
    @GetMapping("/course/{id}")
    fun getCourse(@PathVariable id: String): CourseInfo {
        return CourseInfo(name = id, offeringTerm = Term.WINTER)
    }
}