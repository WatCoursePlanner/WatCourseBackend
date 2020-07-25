package com.watcourses.wat_courses.rules

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.watcourses.wat_courses.persistence.DbRuleRepo
import com.watcourses.wat_courses.proto.RuleImportRequest
import com.watcourses.wat_courses.proto.RuleImportResponse
import org.springframework.stereotype.Component

@Component
class RuleImporter(private val dbRuleRepo: DbRuleRepo) {
    fun import(request: RuleImportRequest): RuleImportResponse {
        val resp = mutableMapOf<String, String>()
        for (item in request.items) {
            try {
                val rule = dbRuleRepo.findFirstByRawRuleOrderById(item.rawRule!!)!!
                rule.manuallyAssigned = true
                rule.cond = ConditionParser.parseToEnd(item.condition!!)
                rule.fullyResolved = item.completelyParsed!!
                dbRuleRepo.save(rule)
            } catch (e: Exception) {
                resp[item.rawRule ?: "null"] = e.toString()
            }
        }
        return RuleImportResponse(result = resp)
    }
}
