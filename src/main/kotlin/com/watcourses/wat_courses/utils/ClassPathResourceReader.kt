package com.watcourses.wat_courses.utils

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class ClassPathResourceReader {
    fun get(path: String) = ClassPathResource(path)
}
