package com.watcourses.wat_courses.persistence

import com.vladmihalcea.hibernate.type.json.JsonStringType
import com.watcourses.wat_courses.proto.CourseInfo
import com.watcourses.wat_courses.proto.Term
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.math.RoundingMode
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

    @OneToOne(fetch = FetchType.EAGER) @JoinColumn
    var preRequisite: DbRule?,

    @OneToOne(fetch = FetchType.EAGER) @JoinColumn
    var coRequisite: DbRule?,

    @OneToOne(fetch = FetchType.EAGER) @JoinColumn
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
    private fun Double.round(precision: Int) = this.toBigDecimal().setScale(precision, RoundingMode.HALF_UP).toDouble()

    fun toProto(basicInfoOnly: Boolean = false): CourseInfo {
        val basicInfo = CourseInfo(
            name = name,
            code = code,
            id = courseId,
            liked = liked?.round(3),
            useful = useful?.round(3),
            easy = easy?.round(3),
            commentsCount = commentCount,
            ratingsCount = filledCount
        )
        if (basicInfoOnly) return basicInfo

        return basicInfo.copy(
            description = description,
            offeringTerms = offeringTerms ?: listOf(),
            preRequisite = preRequisite?.toProto(),
            coRequisite = coRequisite?.toProto(),
            antiRequisite = antiRequisite?.toProto()
        )
    }

    companion object {
        fun toBasicInfoProto(proto: CourseInfo): CourseInfo {
            return CourseInfo(
                name = proto.name,
                code = proto.code,
                id = proto.id,
                liked = proto.liked,
                useful = proto.useful,
                easy = proto.easy,
                commentsCount = proto.commentsCount,
                ratingsCount = proto.ratingsCount
            )
        }
    }
}
