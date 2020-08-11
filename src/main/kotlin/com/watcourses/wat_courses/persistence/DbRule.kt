package com.watcourses.wat_courses.persistence

import com.google.gson.Gson
import com.vladmihalcea.hibernate.type.json.JsonStringType
import com.watcourses.wat_courses.proto.RuleInfo
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

    @Column(nullable = false)
    var fullyResolved: Boolean = false,

    @Id @GeneratedValue
    var id: Long? = null
) {
    companion object {
        fun parse(raw: String, parser: (String) -> Pair<Condition, Boolean>): DbRule {
            return try {
                val (condition, fullyResolved) = parser(raw)
                DbRule(rawRule = raw, cond = condition, fullyResolved = fullyResolved)
            } catch (e: RawConditionParser.ParseFailure) {
                DbRule(rawRule = raw, cond = null, parseFailureBecause = e.message.toString())
            }
        }

        fun findOrParse(raw: String, dbRuleRepo: DbRuleRepo, parser: (String) -> Pair<Condition, Boolean>) =
            dbRuleRepo.findFirstByRawRuleOrderById(raw) ?: parse(raw, parser)
    }

    fun toProto(): RuleInfo {
        return RuleInfo(
            rawString = rawRule,
            logicString = cond.toString(),
            fullyResolved = fullyResolved,
            json = Gson().toJson(cond)
        )
    }
}
