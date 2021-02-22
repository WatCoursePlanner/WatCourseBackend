package com.watcourses.wat_courses.utils

import com.watcourses.wat_courses.persistence.DbCourseRepo
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
class CachedData(private val dbCourseRepo: DbCourseRepo) {
    @Cacheable("courses")
    fun allCourses() = dbCourseRepo.findAll().map { it!!.toProto() }

    @CacheEvict("courses")
    fun invalidateAllCourses() {}
}
