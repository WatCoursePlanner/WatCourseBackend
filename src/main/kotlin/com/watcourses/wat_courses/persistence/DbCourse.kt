package com.watcourses.wat_courses.persistence

import Term
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class DbCourse(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var code: String,

    @Column
    var offeringTerm: Term?,

    @Column(nullable = false)
    var faculty: String,

    @Id @GeneratedValue
    var id: Long? = null
)