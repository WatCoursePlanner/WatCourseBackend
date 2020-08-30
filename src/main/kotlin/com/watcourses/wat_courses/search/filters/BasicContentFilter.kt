package com.watcourses.wat_courses.search.filters

import com.watcourses.wat_courses.proto.CourseInfo
import org.springframework.stereotype.Component

@Component
class BasicContentFilter : SearchFilter {
    override fun match(course: CourseInfo, query: String): Boolean {
        return course.name!!.contains(query, ignoreCase = true) ||
                course.description!!.contains(query, ignoreCase = true) ||
                course.code!!.contains(query, ignoreCase = true)
    }
}