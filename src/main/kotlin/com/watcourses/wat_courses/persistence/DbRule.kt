package com.watcourses.wat_courses.persistence

import com.vladmihalcea.hibernate.type.json.JsonStringType
import com.watcourses.wat_courses.rules.Condition
import com.watcourses.wat_courses.rules.RawConditionParser
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.*

@Entity(name = "rules")
@TypeDef(name = "json", typeClass = JsonStringType::class)
@Table(
    name = "rules", indexes = [
        Index(name = "idx_manually_assigned", columnList = "manuallyAssigned", unique = false)
    ]
)
data class DbRule(
    @Column(length = 1024)
    var rawRule: String?,

    @Column(columnDefinition = "longtext") @Type(type = "json")
    var cond: Condition?,

    @Column(nullable = false)
    var manuallyAssigned: Boolean = false,

    @Column(columnDefinition = "text")
    var parseFailureBecause: String? = null,

    @Id @GeneratedValue
    var id: Long? = null
) {
    companion object {
        fun parse(raw: String, parser: (String) -> Condition): DbRule {
            return try {
                DbRule(rawRule = raw, cond = parser(raw))
            } catch (e: RawConditionParser.ParseFailure) {
                DbRule(rawRule = raw, cond = null, parseFailureBecause = e.message.toString())
            }
        }

        fun findOrParse(raw: String, dbRuleRepo: DbRuleRepo, parser: (String) -> Condition)
                = dbRuleRepo.findFirstByRawRuleOrderById(raw) ?: parse(raw, parser)
    }
}
