package com.watcourses.wat_courses.persistence

import com.watcourses.wat_courses.proto.Schedule
import com.watcourses.wat_courses.proto.StudentProfile
import javax.persistence.*

@Entity(name = "student_profile")
@Table(name = "student_profiles")
data class DbStudentProfile(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var schedule: DbStudentProfileSchedule,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "student_profile_labels", joinColumns = [JoinColumn(name = "student_profile_id")])
    @Column(name = "degree")
    var labels: MutableSet<String> = mutableSetOf(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "student_profile_degrees", joinColumns = [JoinColumn(name = "student_profile_id")])
    @Column(name = "degree")
    var degrees: MutableSet<String> = mutableSetOf(),

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn
    var shortListCourses: MutableList<DbCourse> = mutableListOf(),

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var owner: DbUser? = null,

    @Id @GeneratedValue
    var id: Long? = null
) {
    fun toProto(): StudentProfile {
        return StudentProfile(
            schedule = schedule.toProto(),
            labels = labels.toList(),
            degrees = degrees.toList(),
            shortList = shortListCourses.map { it.code },
            ownerEmail = owner?.email,
        )
    }

    companion object {
        fun create(
            dbStudentProfileRepo: DbStudentProfileRepo,
            schedule: DbStudentProfileSchedule,
            labels: MutableList<String>,
            degrees: MutableList<String>,
            shortList: MutableList<DbCourse> = mutableListOf(),
            owner: DbUser?,
        ): DbStudentProfile {
            val dbStudentProfile = DbStudentProfile(
                schedule = schedule,
                labels = labels.toMutableSet(),
                degrees = degrees.toMutableSet(),
                shortListCourses = shortList,
                owner = owner,
            )
            dbStudentProfileRepo.save(dbStudentProfile)
            return dbStudentProfile
        }
    }
}
