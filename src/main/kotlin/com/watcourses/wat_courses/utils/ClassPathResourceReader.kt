package com.watcourses.wat_courses.utils

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Component

@Component
class ClassPathResourceReader {
    fun get(path: String) = ClassPathResource(path)
    fun getResources(path: String) = PathMatchingResourcePatternResolver(this.javaClass.classLoader).getResources(path)
}
