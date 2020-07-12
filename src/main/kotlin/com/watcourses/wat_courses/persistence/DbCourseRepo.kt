package com.watcourses.wat_courses.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository

interface DbCourseRepo : CrudRepository<DbCourse?, Long?> {
    fun findAll(pageable: Pageable): Page<DbCourse>
    fun findByCode(code: String): DbCourse?
    fun findByCourseId(courseId: String): DbCourse?
    fun findAllByCodeStartingWith(codeLike: String): List<DbCourse>
}