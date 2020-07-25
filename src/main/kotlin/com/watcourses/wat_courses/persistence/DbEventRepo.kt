package com.watcourses.wat_courses.persistence

import org.springframework.data.repository.CrudRepository

interface DbEventRepo : CrudRepository<DbEvent?, Long?>
