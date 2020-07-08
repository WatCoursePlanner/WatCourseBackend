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
        val results = mutableListOf<String>()
        val coursesTaken = mutableSetOf<String>()
        for (term in profile.schedule!!.terms) {
            val effectiveLabels = profile.labels.toSet() + TermResolver.getApplicableLabels(term.termName!!)
            for (course in term.courseCodes) {
                val state = StudentState(coursesTaken = coursesTaken, labels = effectiveLabels)
                val stateIncludingThisTerm =
                    StudentState(coursesTaken = coursesTaken + term.courseCodes, labels = effectiveLabels)
                val dbCourse = dbCourseRepo.findByCode(course) ?: throw RuntimeException("Course $course not found")
                if (dbCourse.preRequisite?.cond?.check(state) == false) {
                    results.add("The pre-requisite of $course is not met.")
                }
                if (dbCourse.coRequisite?.cond?.check(stateIncludingThisTerm) == false) {
                    results.add("The co-requisite of $course is not met.")
                }
                if (dbCourse.antiRequisite?.cond?.check(stateIncludingThisTerm) == true) {
                    results.add("The anti-requisite of $course is not met.")
                }
                coursesTaken.addAll(term.courseCodes)
            }
        }
        return CheckResults(errors = results)
    }
}