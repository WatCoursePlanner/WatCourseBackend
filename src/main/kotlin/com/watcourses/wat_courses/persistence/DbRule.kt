package com.watcourses.wat_courses.persistence

import com.vladmihalcea.hibernate.type.json.JsonStringType
import com.watcourses.wat_courses.rules.Condition
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity(name = "rules")
@TypeDef(name = "json", typeClass = JsonStringType::class)
data class DbRule(
    @Column(length = 1024)
    var rawRule: String?,

    @Column(nullable = false, columnDefinition = "json") @Type(type = "json")
    var cond: Condition,

    @Column(nullable = false)
    var manuallyAssigned: Boolean = false,

    @Id @GeneratedValue
    var id: Long? = null
) {
    companion object {
        fun parse(raw: String) = DbRule(rawRule = raw, cond = Condition.parseFromText(raw))
    }
}
