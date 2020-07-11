package com.watcourses.wat_courses.persistence

import org.springframework.data.repository.CrudRepository

interface DbCourseScheduleRepo : CrudRepository<DbCourseSchedule?, Long?> {
    fun findByCodeAndTermId(code: String, termId: String): DbCourseSchedule?
}