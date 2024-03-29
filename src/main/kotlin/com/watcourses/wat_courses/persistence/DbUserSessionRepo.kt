package com.watcourses.wat_courses.persistence

import org.springframework.data.repository.CrudRepository

interface DbUserSessionRepo : CrudRepository<DbUserSession?, Long?> {
    fun findBySessionToken(sessionToken: String): DbUserSession?
}