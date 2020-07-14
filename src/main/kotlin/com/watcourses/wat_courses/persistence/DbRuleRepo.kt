package com.watcourses.wat_courses.persistence

import org.springframework.data.repository.CrudRepository

interface DbRuleRepo : CrudRepository<DbRule?, Long?> {
    fun findFirstByRawRuleOrderById(rawRule: String): DbRule?
    fun findAllByCondIsNull(): Collection<DbRule>
    fun findAllByCondIsNotNull(): Collection<DbRule>
}