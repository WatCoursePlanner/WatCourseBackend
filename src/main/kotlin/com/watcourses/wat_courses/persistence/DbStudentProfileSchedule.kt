package com.watcourses.wat_courses.persistence

import com.watcourses.wat_courses.proto.Schedule
import com.watcourses.wat_courses.proto.Term
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import javax.persistence.*

@Entity(name = "student_profile_schedule")
@Table(name = "student_profile_schedules")
data class DbStudentProfileSchedule(
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id")
    var terms: MutableList<DbTermSchedule> = mutableListOf(),

    @Id @GeneratedValue
    var id: Long? = null,
) {
    fun toProto(): Schedule {
        return Schedule(
            terms = terms.map { it.toProto() },
        )
    }

    companion object {
        fun createOrUpdate(
            dbStudentProfileScheduleRepo: DbStudentProfileScheduleRepo,
            dbTermScheduleRepo: DbTermScheduleRepo,
            dbCourseRepo: DbCourseRepo,
            schedule: Schedule,
            existingDbStudentProfileSchedule: DbStudentProfileSchedule?,
        ): DbStudentProfileSchedule {
            val dbStudentProfileSchedule = existingDbStudentProfileSchedule ?: DbStudentProfileSchedule()
            val newTerms = schedule.terms.map { termSchedule ->
                DbTermSchedule.createOrUpdate(
                    dbTermScheduleRepo = dbTermScheduleRepo,
                    dbCourseRepo = dbCourseRepo,
                    termSchedule = termSchedule,
                    existingDbTermSchedule = dbStudentProfileSchedule.terms
                        .singleOrNull { it.name == termSchedule.termName },
                )
            }.toMutableList()

            val newTermNames = newTerms.map { it.name }.toSet()
            dbStudentProfileSchedule.terms
                .filter { it.name !in newTermNames }
                .forEach { dbTermScheduleRepo.delete(it) }

            dbStudentProfileSchedule.terms = newTerms
            return dbStudentProfileScheduleRepo.save(dbStudentProfileSchedule)
        }
    }
}
