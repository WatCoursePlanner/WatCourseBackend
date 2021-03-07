package com.watcourses.wat_courses.persistence

import com.watcourses.wat_courses.proto.Schedule
import com.watcourses.wat_courses.proto.StudentProfile
import javax.persistence.*

@Entity(name = "student_profile")
@Table(name = "student_profiles")
data class DbStudentProfile(
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn
    var terms: MutableList<DbTermSchedule> = mutableListOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_profile_labels", joinColumns = [JoinColumn(name = "student_profile_id")])
    @Column(name = "degree")
    var labels: MutableList<String> = mutableListOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_profile_degrees", joinColumns = [JoinColumn(name = "student_profile_id")])
    @Column(name = "degree")
    var degrees: MutableList<String> = mutableListOf(),

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn
    var shortListCourses: MutableList<DbCourse> = mutableListOf(),

    @Id @GeneratedValue
    var id: Long? = null
) {
    fun toProto(): StudentProfile {
        return StudentProfile(
            schedule = Schedule(
                terms = terms.map { it.toProto() },
            ),
            labels = labels,
            degrees = degrees,
            shortList = shortListCourses.map { it.code }
        )
    }
}
