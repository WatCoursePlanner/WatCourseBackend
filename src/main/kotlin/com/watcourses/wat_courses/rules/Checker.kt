package com.watcourses.wat_courses.rules

import CheckResults
import StudentProfile
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Checker {
    @Autowired
    private lateinit var dbCourseRepo: DbCourseRepo

    private fun generateIssue(course: String, rule: DbRule, type: CheckResults.Issue.Type): CheckResults.Issue {
        return CheckResults.Issue(
            type = type,
            subjectName = course,
            relatedCond = rule.cond.toString(),
            relatedCondRaw = rule.rawRule,
            relatedCourse = rule.cond!!.getRelatedCourses().toList(),
            relatedCourseList = rule.cond!!.getRelatedCourses().toList()
        )
    }

    fun check(profile: StudentProfile): CheckResults {
        val issues = mutableListOf<CheckResults.Issue>()
        val coursesTaken = mutableSetOf<String>()
        for (term in profile.schedule!!.terms) {
            val effectiveLabels = profile.labels.toSet() + TermResolver.getApplicableLabels(term.termName!!)
            for (course in term.courseCodes) {
                val state = StudentState(coursesTaken = coursesTaken, labels = effectiveLabels)
                val stateIncludingThisTerm =
                    StudentState(coursesTaken = coursesTaken + term.courseCodes, labels = effectiveLabels)
                val dbCourse = dbCourseRepo.findByCode(course) ?: throw RuntimeException("Course $course not found")
                if (dbCourse.preRequisite?.cond?.check(state) == false) {
                    issues.add(
                        generateIssue(course, dbCourse.preRequisite!!, CheckResults.Issue.Type.PRE_REQUISITE_NOT_MET)
                    )
                }
                if (dbCourse.coRequisite?.cond?.check(stateIncludingThisTerm) == false) {
                    issues.add(
                        generateIssue(course, dbCourse.coRequisite!!, CheckResults.Issue.Type.CO_REQUISITE_NOT_MET)
                    )
                }
                if (dbCourse.antiRequisite?.cond?.check(stateIncludingThisTerm) == true) {
                    issues.add(
                        generateIssue(course, dbCourse.antiRequisite!!, CheckResults.Issue.Type.ANTI_REQUISITE_NOT_MET)
                    )
                }
                coursesTaken.addAll(term.courseCodes)
            }
        }
        return CheckResults(issues = issues)
    }
}