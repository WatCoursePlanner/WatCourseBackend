package com.watcourses.wat_courses.persistence

import com.watcourses.wat_courses.proto.Schedule
import com.watcourses.wat_courses.proto.Term
import javax.persistence.*

@Entity(name = "term_schedule")
@Table(name = "term_schedules")
data class DbTermSchedule(
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn
    var courses: MutableList<DbCourse> = mutableListOf(),

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var year: Int,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var term: Term,

    @Id @GeneratedValue
    var id: Long? = null
) {
    fun toProto(): Schedule.TermSchedule {
        return Schedule.TermSchedule(
            courseCodes = courses.map { it.code },
            termName = name,
            year = year,
            term = term
        )
    }
}
