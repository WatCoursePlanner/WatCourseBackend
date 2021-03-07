package com.watcourses.wat_courses.persistence

import com.vladmihalcea.hibernate.type.json.JsonStringType
import com.watcourses.wat_courses.proto.CourseSchedule
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.*

@Entity(name = "schedule")
@Table(
    name = "schedules", indexes = [
        Index(name = "idx_code,termId", columnList = "code,termId", unique = true)
    ]
)
@TypeDef(name = "json", typeClass = JsonStringType::class)
data class DbCourseSchedule(
    @Column(nullable = false)
    var code: String,

    @Column
    var termId: String,

    @Column(columnDefinition = "json") @Type(type = "json")
    var sections: List<CourseSchedule>,

    @Column
    var enrolledTotal: Int,

    @Column
    var enrolledCap: Int,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var owner: DbUser? = null,

    @Id @GeneratedValue
    var id: Long? = null
)