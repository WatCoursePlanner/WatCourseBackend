package com.watcourses.wat_courses.persistence

import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity(name = "user_session")
@Table(
    name = "user_sessions", indexes = [
        Index(name = "idx_session", columnList = "sessionToken", unique = true),
    ]
)
data class DbUserSession(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @Column(updatable = false, nullable = false)
    var sessionToken: String,

    @ManyToOne
    @JoinColumn
    var user: DbUser? = null,

    @Column
    var expiresAt: Date? = null,
)
