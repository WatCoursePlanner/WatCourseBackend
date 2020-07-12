package com.watcourses.wat_courses.persistence

import com.watcourses.wat_courses.proto.Courses.CourseInfo
import com.watcourses.wat_courses.proto.Courses.Term
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
        val builder = CourseInfo.newBuilder()
                .setName(name)
                .setCode(code)
                .setDescription(description)
                .setId(courseId)
                .setPreRequisite(preRequisite?.rawRule)
                .setCoRequisite(coRequisite?.rawRule)
                .setAntiRequisite(antiRequisite?.rawRule)
                .setPreRequisiteLogicStr(preRequisite?.cond?.toString())
                .setCoRequisiteLogicStr(coRequisite?.cond?.toString())
                .setAntiRequisiteLogicStr(antiRequisite?.cond?.toString())

        if (offeringTerms != null) {
            for ((index, term) in offeringTerms!!.withIndex()) {
                builder.setOfferingTerms(index, term)
            }
        }

        return builder.build()
//        return CourseInfo(
//            name = name,
//            code = code,
//            description = description,
//            offeringTerms = offeringTerms ?: listOf(),
//            id = courseId,
//            preRequisite = preRequisite?.rawRule,
//            coRequisite = coRequisite?.rawRule,
//            antiRequisite = antiRequisite?.rawRule,
//            preRequisiteLogicStr = preRequisite?.cond?.toString(),
//            coRequisiteLogicStr = coRequisite?.cond?.toString(),
//            antiRequisiteLogicStr = antiRequisite?.cond?.toString()
//        )
    }
}
