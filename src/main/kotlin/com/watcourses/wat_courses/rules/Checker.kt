package com.watcourses.wat_courses.rules

import CheckResults
import StudentProfile
import com.watcourses.wat_courses.persistence.DbCourseRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Checker {
    @Autowired
    private lateinit var dbCourseRepo: DbCourseRepo

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
                        CheckResults.Issue(
                            type = CheckResults.Issue.Type.PRE_REQUISITE_NOT_MET,
                            subjectName = course,
                            relatedCond = dbCourse.preRequisite!!.cond.toString(),
                            relatedCondRaw = dbCourse.preRequisite!!.rawRule,
                            relatedCourse = dbCourse.preRequisite!!.cond!!.getRelatedCourses().toList(),
                            relatedCourseList = dbCourse.preRequisite!!.cond!!.getRelatedCourses().toList()
                        )
                    )
                }
                if (dbCourse.coRequisite?.cond?.check(stateIncludingThisTerm) == false) {
                    issues.add(
                        CheckResults.Issue(
                            type = CheckResults.Issue.Type.CO_REQUISITE_NOT_MET,
                            subjectName = course,
                            relatedCond = dbCourse.coRequisite!!.cond.toString(),
                            relatedCondRaw = dbCourse.coRequisite!!.rawRule,
                            relatedCourse = dbCourse.coRequisite!!.cond!!.getRelatedCourses().toList(),
                            relatedCourseList = dbCourse.coRequisite!!.cond!!.getRelatedCourses().toList()
                        )
                    )
                }
                if (dbCourse.antiRequisite?.cond?.check(stateIncludingThisTerm) == true) {
                    issues.add(
                        CheckResults.Issue(
                            type = CheckResults.Issue.Type.ANTI_REQUISITE_NOT_MET,
                            subjectName = course,
                            relatedCond = dbCourse.antiRequisite!!.cond.toString(),
                            relatedCondRaw = dbCourse.antiRequisite!!.rawRule,
                            relatedCourse = dbCourse.antiRequisite!!.cond!!.getRelatedCourses().toList(),
                            relatedCourseList = dbCourse.antiRequisite!!.cond!!.getRelatedCourses().toList()
                        )
                    )
                }
                coursesTaken.addAll(term.courseCodes)
            }
        }
        return CheckResults(issues = issues)
    }
}