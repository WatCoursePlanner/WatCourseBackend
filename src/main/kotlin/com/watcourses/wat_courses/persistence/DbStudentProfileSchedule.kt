package com.watcourses.wat_courses.persistence

import com.watcourses.wat_courses.proto.Schedule
import com.watcourses.wat_courses.proto.Term
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.*

@Entity(name = "student_profile_schedule")
@Table(name = "student_profile_schedules")
data class DbStudentProfileSchedule(
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn
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
        fun create(
            dbStudentProfileScheduleRepo: DbStudentProfileScheduleRepo,
            terms: MutableList<DbTermSchedule>,
        ): DbStudentProfileSchedule {
            val dbStudentProfileSchedule = DbStudentProfileSchedule(
                terms = terms,
            )
            dbStudentProfileScheduleRepo.save(dbStudentProfileSchedule)
            return dbStudentProfileSchedule
        }

        fun create(
            dbStudentProfileScheduleRepo: DbStudentProfileScheduleRepo,
            dbTermScheduleRepo: DbTermScheduleRepo,
            dbCourseRepo: DbCourseRepo,
            schedule: Schedule,
        ): DbStudentProfileSchedule {
            val dbStudentProfileSchedule = DbStudentProfileSchedule(
                terms = schedule.terms.map {
                    DbTermSchedule.create(
                        dbTermScheduleRepo = dbTermScheduleRepo,
                        dbCourseRepo = dbCourseRepo,
                        termSchedule = it,
                    )
                }.toMutableList(),
            )
            dbStudentProfileScheduleRepo.save(dbStudentProfileSchedule)
            return dbStudentProfileSchedule
        }
    }
}
