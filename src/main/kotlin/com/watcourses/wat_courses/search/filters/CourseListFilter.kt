package com.watcourses.wat_courses.search.filters

import com.watcourses.wat_courses.proto.CourseInfo
import com.watcourses.wat_courses.rules.CourseListLoader
import org.springframework.stereotype.Component

@Component
class CourseListFilter(private val courseLists: CourseListLoader) : SearchFilter {
    override fun match(course: CourseInfo, query: String): Boolean {
        return courseLists.getListOrNull(query)?.courses?.contains(course.code!!) == true
    }
}
