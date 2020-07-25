package com.watcourses.wat_courses.persistence

import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.TypeDef
import javax.persistence.*

@Entity(name = "event")
@Table(
    name = "events", indexes = [
        Index(name = "idx_type", columnList = "type", unique = false),
        Index(name = "idx_subject", columnList = "subject", unique = false)
    ]
)
@TypeDef(name = "json", typeClass = JsonStringType::class)
data class DbEvent(
    @Column(nullable = false)
    var type: String,

    @Column(nullable = false)
    var subject: String,

    @Column(nullable = false)
    var identifier: String,

    @Column
    var data: String?,

    @Id @GeneratedValue
    var id: Long? = null
)