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

    @Column
    var name: String? = null,

    @Column
    var year: Int? = null,

    @Column
    @Enumerated(EnumType.STRING)
    var term: Term? = null,

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

    companion object {
        fun create(
            dbTermScheduleRepo: DbTermScheduleRepo,
            courses: MutableList<DbCourse>,
            name: String,
            year: Int,
            term: Term,
        ): DbTermSchedule {
            val dbTermSchedule = DbTermSchedule(
                courses = courses,
                name = name,
                year = year,
                term = term,
            )
            dbTermScheduleRepo.save(dbTermSchedule)
            return dbTermSchedule
        }

        fun create(
            dbTermScheduleRepo: DbTermScheduleRepo,
            dbCourseRepo: DbCourseRepo,
            termSchedule: Schedule.TermSchedule,
        ): DbTermSchedule {
            val orderOf = termSchedule.courseCodes.mapIndexed { index, code -> code to index }.toMap()
            val dbCourses = dbCourseRepo.findAllByCodeIn(termSchedule.courseCodes).sortedBy { orderOf[it.code] }
            val dbTermSchedule = DbTermSchedule(
                courses = dbCourses.toMutableList(),
                name = termSchedule.termName,
                year = termSchedule.year,
                term = termSchedule.term,
            )
            dbTermScheduleRepo.save(dbTermSchedule)
            return dbTermSchedule
        }
    }
}
