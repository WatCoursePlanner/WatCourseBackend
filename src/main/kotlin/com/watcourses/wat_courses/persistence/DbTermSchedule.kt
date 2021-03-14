package com.watcourses.wat_courses.persistence

import com.watcourses.wat_courses.proto.Schedule
import com.watcourses.wat_courses.proto.Term
import org.springframework.data.repository.findByIdOrNull
import javax.persistence.*

@Entity(name = "term_schedule")
@Table(name = "term_schedules")
data class DbTermSchedule(
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "term_schedule_courses")
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

        fun createOrUpdate(
            dbTermScheduleRepo: DbTermScheduleRepo,
            dbCourseRepo: DbCourseRepo,
            termSchedule: Schedule.TermSchedule,
            existingDbTermSchedule: DbTermSchedule?,
        ): DbTermSchedule {
            val orderOf = termSchedule.courseCodes.mapIndexed { index, code -> code to index }.toMap()
            val dbCourses = dbCourseRepo.findAllByCodeIn(termSchedule.courseCodes).sortedBy { orderOf[it.code] }
            val dbTermSchedule = existingDbTermSchedule ?: DbTermSchedule()

            dbTermSchedule.courses = dbCourses.toMutableList()
            dbTermSchedule.name = termSchedule.termName
            dbTermSchedule.year = termSchedule.year
            dbTermSchedule.term = termSchedule.term

            return dbTermScheduleRepo.save(dbTermSchedule)
        }
    }
}
