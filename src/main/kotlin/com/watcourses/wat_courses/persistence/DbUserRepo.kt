package com.watcourses.wat_courses.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository

interface DbUserRepo : CrudRepository<DbUser?, Long?> {
    fun findByEmail(email: String): DbUser?
    fun findBySessionId(sessionId: String): DbUser?
    fun findByGoogleId(googleId: String): DbUser?
}