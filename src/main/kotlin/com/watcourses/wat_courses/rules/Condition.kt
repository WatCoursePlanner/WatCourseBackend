package com.watcourses.wat_courses.rules

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.watcourses.wat_courses.proto.ConditionType
import com.watcourses.wat_courses.utils.unionFlatten

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Condition(
    val type: ConditionType,
    val operands: List<Condition>,
    val data: String? = null,
    var met: Boolean? = false
) {
    // Get the set of courses involved in the condition
    fun getRelatedCourses(): Set<String> {
        return (if (type == ConditionType.HAS_COURSE) setOf(data!!) else setOf()) +
                (operands.map { it.getRelatedCourses() }.unionFlatten())
    }

    // Get the set of course lists involved in the condition
    fun getRelatedCourseLists(): Set<String> {
        val courses = if (type == ConditionType.SATISFIES_LIST) setOf(data!!.split(":")[0]) else setOf()
        return courses + (operands.map { it.getRelatedCourseLists() }.unionFlatten())
    }

    // Get the set of labels involved in the condition
    fun getRelatedLabels(): Set<String> {
        return (if (type == ConditionType.HAS_LABEL) setOf(data!!) else setOf()) +
                (operands.map { it.getRelatedLabels() }.unionFlatten())
    }

    fun addOperand(operand: Condition) =
        Condition(type = type, operands = operands + operand, data = data)

    fun minify(): Condition {
        if (type != ConditionType.AND && type != ConditionType.OR) return this
        var minifiedOperands = operands.map { it.minify() }
        val operandsWithSameType = minifiedOperands.filter { it.type == type }
        minifiedOperands = minifiedOperands.filterNot { it.type == type }.toMutableList().apply {
            addAll(operandsWithSameType.map { it.operands.map { operand -> operand.minify() } }.flatten())
            return@apply
        }
        if (minifiedOperands.size == 1) return minifiedOperands.single()
        return this.copy(operands = minifiedOperands)
    }

    private fun toStringInternal(): String {
        val bracketIfComplex = { c: String -> if (c.contains("&&") || c.contains("||")) "($c)" else c }
        return when (type) {
            ConditionType.TRUE -> "true"
            ConditionType.FALSE -> "false"
            ConditionType.AND -> bracketIfComplex(operands.joinToString(" && ") { it.toStringInternal() })
            ConditionType.OR -> bracketIfComplex(operands.joinToString(" || ") { it.toStringInternal() })
            ConditionType.NOT -> "!${operands.single().toStringInternal()}"
            ConditionType.HAS_COURSE -> "$data"
            ConditionType.HAS_LABEL -> "[$data]"
            ConditionType.SATISFIES_LIST -> "<$data>"
        }
    }

    override fun toString(): String {
        val ret = toStringInternal()
        return if (ret.first() == '(' && ret.last() == ')') ret.substring(1, ret.length - 1) else ret
    }

    companion object // for extensions
}
