package com.watcourses.wat_courses.search.filters

import com.watcourses.wat_courses.proto.CourseInfo
import org.springframework.stereotype.Component

@Component
class BasicContentFilter : SearchFilter {
    override fun match(course: CourseInfo, query: String): Boolean {
        return matchString(course.name!!, query) ||
            matchString(course.description!!, query) ||
            matchString(course.code!!, query)
    }

    private fun matchString(content: String, query: String): Boolean {
        return content.contains(query, ignoreCase = true)
    }
}