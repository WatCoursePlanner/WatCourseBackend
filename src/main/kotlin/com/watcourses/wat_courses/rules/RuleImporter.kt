package com.watcourses.wat_courses.rules

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.watcourses.wat_courses.persistence.DbRuleRepo
import com.watcourses.wat_courses.proto.ConditionType
import com.watcourses.wat_courses.proto.RuleImportRequest
import com.watcourses.wat_courses.proto.RuleImportResponse
import org.springframework.stereotype.Component

@Component
class RuleImporter(private val dbRuleRepo: DbRuleRepo, private val rawConditionParser: RawConditionParser) {
    private fun checkConditionValid(cond: Condition) {
        if (cond.type == ConditionType.HAS_LABEL
            && !ALL_LABELS.contains(cond.data)
            && !cond.data!!.matches(Regex("[1-4][A-B]"))
        ) throw Exception("Label ${cond.data} not found")

        if (cond.type == ConditionType.HAS_COURSE)
            rawConditionParser.courseSanityCheck(cond.data!!)

        for (operand in cond.operands) checkConditionValid(operand)
    }

    fun import(request: RuleImportRequest): RuleImportResponse {
        val resp = mutableMapOf<String, String>()
        for (item in request.items) {
            try {
                val rule = dbRuleRepo.findFirstByRawRuleOrderById(item.rawRule!!)!!
                rule.manuallyAssigned = true
                rule.cond = ConditionParser.parseToEnd(item.condition!!)
                checkConditionValid(rule.cond!!)
                rule.fullyResolved = item.completelyParsed!!
                dbRuleRepo.save(rule)
            } catch (e: Exception) {
                resp[item.rawRule ?: "null"] = e.message.toString()
            }
        }
        return RuleImportResponse(result = resp)
    }
}
