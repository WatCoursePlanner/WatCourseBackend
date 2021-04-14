package com.watcourses.wat_courses

import com.watcourses.wat_courses.utils.CourseBuilderProvider
import org.springframework.stereotype.Component

@Component
class Utils(private val courseBuilderProvider: CourseBuilderProvider) {
    fun createCourse(vararg courseCodes: String) {
        for (code in courseCodes) courseBuilderProvider.get().code(code).build()
    }
}
