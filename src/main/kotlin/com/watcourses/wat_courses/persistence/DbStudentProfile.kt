package com.watcourses.wat_courses.persistence

import com.watcourses.wat_courses.proto.StudentProfile
import org.springframework.data.repository.findByIdOrNull
import javax.persistence.*

@Entity(name = "student_profile")
@Table(name = "student_profiles")
data class DbStudentProfile(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var schedule: DbStudentProfileSchedule,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "student_profile_labels", joinColumns = [JoinColumn(name = "student_profile_id")])
    @Column(name = "label")
    var labels: MutableList<String> = mutableListOf(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "student_profile_degrees", joinColumns = [JoinColumn(name = "student_profile_id")])
    @Column(name = "degree")
    var degrees: MutableList<String> = mutableListOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_profile_shortlist_courses",
        joinColumns = [JoinColumn(name = "student_profile_id")],
        inverseJoinColumns = [JoinColumn(name = "course_id")],
    )
    var shortListCourses: MutableList<DbCourse> = mutableListOf(),

    // TODO make this non-nullable
    @Column
    var ownerEmail: String? = null,

    @Id @GeneratedValue
    var id: Long? = null
) {
    fun toProto(): StudentProfile {
        return StudentProfile(
            schedule = schedule.toProto(),
            labels = labels.toList(),
            degrees = degrees.toList(),
            shortList = shortListCourses.map { it.code },
            ownerEmail = ownerEmail,
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
                labels = labels,
                degrees = degrees,
                shortListCourses = shortList,
                ownerEmail = owner?.email,
            )
            dbStudentProfileRepo.save(dbStudentProfile)
            return dbStudentProfile
        }

        fun createOrUpdate(
            dbStudentProfileScheduleRepo: DbStudentProfileScheduleRepo,
            dbTermScheduleRepo: DbTermScheduleRepo,
            dbStudentProfileRepo: DbStudentProfileRepo,
            dbCourseRepo: DbCourseRepo,
            studentProfile: StudentProfile,
            owner: DbUser,
        ): DbStudentProfile {
            val existingDbStudentProfile = owner.studentProfile

            val dbSchedule = DbStudentProfileSchedule.createOrUpdate(
                dbStudentProfileScheduleRepo = dbStudentProfileScheduleRepo,
                dbTermScheduleRepo = dbTermScheduleRepo,
                dbCourseRepo = dbCourseRepo,
                schedule = studentProfile.schedule!!,
                existingDbStudentProfileSchedule = existingDbStudentProfile?.schedule,
            )
            val labels = studentProfile.labels.toMutableList()
            val degrees = studentProfile.degrees.toMutableList()
            val shortList = studentProfile.shortList.mapNotNull { dbCourseRepo.findByCode(it) }.toMutableList()

            if (existingDbStudentProfile == null) {
                return create(
                    dbStudentProfileRepo = dbStudentProfileRepo,
                    schedule = dbSchedule,
                    labels = labels,
                    degrees = degrees,
                    shortList = shortList,
                    owner = owner,
                )
            }

            existingDbStudentProfile.schedule = dbSchedule
            existingDbStudentProfile.labels = labels
            existingDbStudentProfile.degrees = degrees
            existingDbStudentProfile.shortListCourses = shortList
            existingDbStudentProfile.ownerEmail = owner.email

            return dbStudentProfileRepo.save(existingDbStudentProfile)
        }
    }
}
