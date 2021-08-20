package com.watcourses.wat_courses.persistence

import com.watcourses.wat_courses.proto.Schedule
import com.watcourses.wat_courses.proto.Term
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
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

    @Component
    class Factory(
        private val dbStudentProfileScheduleRepo: DbStudentProfileScheduleRepo,
        private val dbTermScheduleRepo: DbTermScheduleRepo,
        private val dbTermScheduleFactory: DbTermSchedule.Factory
    ) {
        fun createOrUpdate(
            schedule: Schedule,
            existingDbStudentProfileSchedule: DbStudentProfileSchedule?,
        ): DbStudentProfileSchedule {
            val dbStudentProfileSchedule = existingDbStudentProfileSchedule ?: DbStudentProfileSchedule()
            val newTerms = schedule.terms.map { termSchedule ->
                dbTermScheduleFactory.createOrUpdate(
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
