package com.watcourses.wat_courses.persistence

import com.watcourses.wat_courses.proto.StudentProfile
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var owner: DbUser,

    @Id @GeneratedValue
    var id: Long? = null
) {
    fun toProto(): StudentProfile {
        return StudentProfile(
            schedule = schedule.toProto(),
            labels = labels.toList(),
            degrees = degrees.toList(),
            shortList = shortListCourses.map { it.code },
            ownerEmail = owner.email,
        )
    }

    @Component
    class Factory(
        private val dbStudentProfileRepo: DbStudentProfileRepo,
        private val dbCourseRepo: DbCourseRepo,
        private val dbStudentProfileScheduleFactory: DbStudentProfileSchedule.Factory,
        private val dbUserRepo: DbUserRepo,
    ) {
        fun create(
            schedule: DbStudentProfileSchedule,
            labels: List<String>,
            degrees: List<String>,
            shortList: List<DbCourse> = listOf(),
            owner: DbUser,
        ): DbStudentProfile {
            val dbStudentProfile = DbStudentProfile(
                schedule = schedule,
                labels = labels.toMutableList(),
                degrees = degrees.toMutableList(),
                shortListCourses = shortList.toMutableList(),
                owner = owner,
            )
            dbStudentProfileRepo.save(dbStudentProfile)
            owner.studentProfile = dbStudentProfile
            dbUserRepo.save(owner)
            return dbStudentProfile
        }

        fun createOrUpdate(
            studentProfile: StudentProfile,
            owner: DbUser,
        ): DbStudentProfile {
            val existingDbStudentProfile = owner.studentProfile

            val dbSchedule = dbStudentProfileScheduleFactory.createOrUpdate(
                schedule = studentProfile.schedule!!,
                existingDbStudentProfileSchedule = existingDbStudentProfile?.schedule,
            )
            val labels = studentProfile.labels.toMutableList()
            val degrees = studentProfile.degrees.toMutableList()
            val shortList = studentProfile.shortList.mapNotNull { dbCourseRepo.findByCode(it) }.toMutableList()

            if (existingDbStudentProfile == null) {
                return create(
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
            existingDbStudentProfile.owner = owner

            owner.studentProfile = existingDbStudentProfile
            dbUserRepo.save(owner)

            return dbStudentProfileRepo.save(existingDbStudentProfile)
        }
    }
}
