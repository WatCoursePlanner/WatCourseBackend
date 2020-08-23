package com.watcourses.wat_courses.rules

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbRule
import com.watcourses.wat_courses.proto.CheckResults
import com.watcourses.wat_courses.proto.ConditionType
import com.watcourses.wat_courses.proto.CourseInfo
import com.watcourses.wat_courses.proto.StudentProfile
import com.watcourses.wat_courses.utils.getDegreeLabels
import com.watcourses.wat_courses.utils.getLabels
import org.springframework.stereotype.Service

@Service
class Checker(
    private val dbCourseRepo: DbCourseRepo,
    private val courseListLoader: CourseListLoader,
    private val degreeRequirementLoader: DegreeRequirementLoader
) {
    // check and populate the `met` field
    fun checkCondition(condition: Condition, studentState: StudentState): Boolean {
        condition.met = when (condition.type) {
            ConditionType.TRUE -> true
            ConditionType.FALSE -> false
            ConditionType.AND -> if (condition.operands.isEmpty()) true else condition.operands.map {
                checkCondition(it, studentState)
            }.reduce { a, b -> a && b }
            ConditionType.OR -> if (condition.operands.isEmpty()) true else condition.operands.map {
                checkCondition(it, studentState)
            }.reduce { a, b -> a || b }
            ConditionType.NOT -> !checkCondition(condition.operands.single(), studentState)
            ConditionType.HAS_COURSE -> studentState.coursesTaken.contains(condition.data)
            ConditionType.HAS_LABEL -> studentState.labels.contains(condition.data)
            ConditionType.SATISFIES_LIST -> {
                val (listName, countStr) = condition.data!!.split(":")
                studentState.coursesTaken.count {
                    courseListLoader.listContainsCourse(listName, it)
                } >= countStr.toLong()
            }
        }
        return condition.met!!
    }

    fun Condition.check(state: StudentState) = checkCondition(this, state)

    private fun generateIssue(course: String, rule: DbRule, type: CheckResults.Issue.Type): CheckResults.Issue {
        return CheckResults.Issue(
            type = type,
            subjectName = course,
            relatedCond = rule.cond.toString(),
            relatedCondRaw = rule.rawRule,
            relatedCourse = rule.cond!!.getRelatedCourses().toList(),
            relatedCourseList = rule.cond!!.getRelatedCourseLists().toList()
        )
    }

    private fun generateDegreeRequirementIssue(req: Requirement): CheckResults.Issue {
        return CheckResults.Issue(
            type = CheckResults.Issue.Type.DEGREE_REQUIREMENTS_NOT_MET,
            subjectName = req.name,
            relatedCond = req.condition.toString(),
            relatedCondRaw = null,
            relatedCourse = req.condition.getRelatedCourses().toList(),
            relatedCourseList = req.condition.getRelatedCourseLists().toList()
        )
    }

    fun check(profile: StudentProfile): CheckResults {
        val issues = mutableListOf<CheckResults.Issue>()
        val degrees = profile.degrees.map { degreeRequirementLoader.getDegreeRequirement(it)!! }
        val degreeLabels = profile.getDegreeLabels(degrees)
        val checkedCourses = mutableListOf<CourseInfo>()

        // Check all individual courses
        val coursesTaken = mutableSetOf<String>()
        for (term in profile.schedule!!.terms) {
            val effectiveLabels = profile.getLabels(degreeLabels, term.termName)

            for (course in term.courseCodes) {
                val state = StudentState(coursesTaken = coursesTaken, labels = effectiveLabels)
                val stateIncludingThisTerm =
                    StudentState(coursesTaken = coursesTaken + term.courseCodes, labels = effectiveLabels)
                val dbCourse = dbCourseRepo.findByCode(course) ?: throw RuntimeException("Course $course not found")
                if (dbCourse.preRequisite?.cond?.check(state) == false) {
                    issues.add(
                        generateIssue(
                            course,
                            dbCourse.preRequisite!!,
                            CheckResults.Issue.Type.PRE_REQUISITE_NOT_MET
                        )
                    )
                }
                if (dbCourse.coRequisite?.cond?.check(stateIncludingThisTerm) == false) {
                    issues.add(
                        generateIssue(course, dbCourse.coRequisite!!, CheckResults.Issue.Type.CO_REQUISITE_NOT_MET)
                    )
                }
                if (dbCourse.antiRequisite?.cond?.check(stateIncludingThisTerm) == true) {
                    issues.add(
                        generateIssue(
                            course,
                            dbCourse.antiRequisite!!,
                            CheckResults.Issue.Type.ANTI_REQUISITE_NOT_MET
                        )
                    )
                }
                coursesTaken.addAll(term.courseCodes)
                checkedCourses.add(dbCourse.toProto())
            }
        }
        // Check degree requirements
        for (degree in degrees) {
            val degreeRequirementPerTerm = degree.requirements.filter {
                it.condition.getRelatedLabels()
                    .any { label -> TermResolver.ALL_TERMS_TO_VALUES_MAP.keys.contains(label) }
            }
            val degreeRequirementOverall = degree.requirements.filter {
                !degreeRequirementPerTerm.contains(it)
            }

            for (req in degreeRequirementOverall) {
                if (!req.condition.check(
                        StudentState(
                            coursesTaken = coursesTaken,
                            labels = profile.getLabels(degreeLabels)
                        )
                    )
                ) {
                    issues.add(generateDegreeRequirementIssue(req))
                }
            }

            for (req in degreeRequirementPerTerm) {
                val coursesTakenSoFar = mutableSetOf<String>()
                for (term in profile.schedule.terms) {
                    if (!req.condition.check(
                            StudentState(
                                coursesTaken = coursesTakenSoFar,
                                labels = profile.getLabels(degreeLabels, term.termName)
                            )
                        )
                    ) {
                        issues.add(generateDegreeRequirementIssue(req))
                        break
                    }
                }
            }
        }
        return CheckResults(issues = issues, checkedCourses = checkedCourses)
    }
}