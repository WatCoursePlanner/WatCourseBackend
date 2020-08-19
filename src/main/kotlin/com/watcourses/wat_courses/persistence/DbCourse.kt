package com.watcourses.wat_courses.persistence

import com.vladmihalcea.hibernate.type.json.JsonStringType
import com.watcourses.wat_courses.proto.CourseInfo
import com.watcourses.wat_courses.proto.Term
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.*

@Entity(name = "course")
@Table(
    name = "courses", indexes = [
        Index(name = "idx_code", columnList = "code", unique = true),
        Index(name = "idx_course_id", columnList = "courseId", unique = false)
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

    @Column
    var liked: Double? = null,

    @Column
    var easy: Double? = null,

    @Column
    var useful: Double? = null,

    @Column
    var filledCount: Int? = null,

    @Column
    var commentCount: Int? = null,

    @Id @GeneratedValue
    var id: Long? = null
) {
    fun toProto(basicInfoOnly: Boolean = true): CourseInfo {
        val basicInfo = CourseInfo(
            name = name,
            code = code,
            id = courseId,
            liked = liked,
            useful = useful,
            easy = easy
        )
        if (basicInfoOnly) return basicInfo

        return basicInfo.copy(
            description = description,
            offeringTerms = offeringTerms ?: listOf(),
            preRequisite = preRequisite?.toProto(),
            coRequisite = coRequisite?.toProto(),
            antiRequisite = antiRequisite?.toProto(),
            commentsCount = commentCount,
            ratingsCount = filledCount
        )
    }
}
