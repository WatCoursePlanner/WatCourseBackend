package com.watcourses.wat_courses.search.filters

import com.watcourses.wat_courses.proto.CourseInfo

interface SearchFilter {
    fun match(course: CourseInfo, query: String): Boolean
}