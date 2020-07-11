package com.watcourses.wat_courses.persistence

import com.watcourses.wat_courses.proto.CourseInfo
import com.watcourses.wat_courses.proto.Term
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.*

@Entity(name = "course")
@Table(
    name = "courses", indexes = [
        Index(name = "idx_code", columnList = "code", unique = true),
        Index(name = "idx_course_id", columnList = "id", unique = true)
    ]
)
@TypeDef(name = "json", typeClass = JsonStringType::class)
data class DbCourse(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var code: String,

    @Column(nullable = false, columnDefinition = "text")
    var description: String,

    @Column(columnDefinition = "json") @Type(type = "json")
    var offeringTerms: List<Term>?,

    @OneToOne @JoinColumn
    var preRequisite: DbRule?,

    @OneToOne @JoinColumn
    var coRequisite: DbRule?,

    @OneToOne @JoinColumn
    var antiRequisite: DbRule?,

    @Column
    var courseId: String,

    @Id @GeneratedValue
    var id: Long? = null
) {
    fun toProto(): CourseInfo {
        return CourseInfo(
            name = name,
            code = code,
            description = description,
            offeringTerms = offeringTerms ?: listOf(),
            id = courseId,
            preRequisite = preRequisite?.rawRule,
            coRequisite = coRequisite?.rawRule,
            antiRequisite = antiRequisite?.rawRule,
            preRequisiteLogicStr = preRequisite?.cond?.toString(),
            coRequisiteLogicStr = coRequisite?.cond?.toString(),
            antiRequisiteLogicStr = antiRequisite?.cond?.toString()
        )
    }
}
