package com.watcourses.wat_courses.persistence

import org.springframework.data.repository.CrudRepository

interface DbCourseRepo : CrudRepository<DbCourse?, Long?> {
    fun findByCode(code: String): DbCourse?
}