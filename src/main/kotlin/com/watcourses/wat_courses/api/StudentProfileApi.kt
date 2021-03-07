package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbStudentProfile
import com.watcourses.wat_courses.persistence.DbStudentProfileRepo
import com.watcourses.wat_courses.persistence.DbStudentProfileSchedule
import com.watcourses.wat_courses.persistence.DbStudentProfileScheduleRepo
import com.watcourses.wat_courses.persistence.DbTermScheduleRepo
import com.watcourses.wat_courses.proto.*
import com.watcourses.wat_courses.rules.Checker
import com.watcourses.wat_courses.rules.DegreeRequirementLoader
import com.watcourses.wat_courses.utils.create
import com.watcourses.wat_courses.utils.unionFlatten
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@Transactional
@RestController
class StudentProfileApi(
    private val degreeRequirementLoader: DegreeRequirementLoader,
    private val dbStudentProfileScheduleRepo: DbStudentProfileScheduleRepo,
    private val dbStudentProfileRepo: DbStudentProfileRepo,
    private val dbTermScheduleRepo: DbTermScheduleRepo,
    private val dbCourseRepo: DbCourseRepo,
    private val checker: Checker
) {
    @GetMapping("/profile/default")
    fun getDefaultStudentProfile(program: String): StudentProfile {
        val degreeRequirement = degreeRequirementLoader.getDegreeRequirement(program)!!
        return StudentProfile(
            schedule = degreeRequirement.defaultSchedule,
            degrees = listOf(program),
            labels = degreeRequirement.labels.toList()
        )
    }

    @PostMapping("/profile/create")
    fun createStudentProfile(@RequestBody request: CreateStudentProfileRequest): StudentProfile {
        val degrees = request.degrees
        val degreeRequirements = degrees.map { degreeRequirementLoader.getDegreeRequirement(it)!! }
        val startingYear = request.startingYear!!
        val stream = request.coopStream!!
        val defaultSchedule = degreeRequirements.single { it.defaultSchedule?.terms?.isNotEmpty() == true }
            .defaultSchedule!!
        val importedSchedule = request.schedule
        val mergedSchedule = importedSchedule?.let { mergeSchedule(it, defaultSchedule) } ?: defaultSchedule

        val dbStudentProfile = DbStudentProfile.create(
            dbStudentProfileRepo = dbStudentProfileRepo,
            schedule = DbStudentProfileSchedule.create(
                dbStudentProfileScheduleRepo = dbStudentProfileScheduleRepo,
                dbTermScheduleRepo = dbTermScheduleRepo,
                dbCourseRepo = dbCourseRepo,
                schedule = Schedule.create(mergedSchedule, startingYear, stream),
            ),
            degrees = degrees.toMutableList(),
            labels = degreeRequirements.map { it.labels.toSet() }.unionFlatten().toMutableList(),
            owner = null,
        )

        return dbStudentProfile.toProto()
    }

    @PostMapping("/profile/check")
    fun checkProfile(@RequestBody profile: StudentProfile): CheckResults {
        return checker.check(profile)
    }

    @PostMapping("/profile/find_slots")
    fun findSlots(@RequestBody request: FindSlotRequest): FindSlotResponse {
        val course = request.courseCode!!
        val profile = removeCourseFromSchedule(request.profile!!, course)

        // Check for each term
        val results = profile.schedule!!.terms.mapIndexed { i, term ->
            val modifiedTerm = term.copy(courseCodes = term.courseCodes + course)
            val mutableTerms = profile.schedule.terms.toMutableList()
            mutableTerms[i] = modifiedTerm
            val profileWithCourse = profile.copy(
                profile.schedule.copy(terms = mutableTerms)
            )
            Pair(term.termName!!, checker.check(profileWithCourse))
        }.toMap()

        // Extract common issues
        var commonResults = results.values.firstOrNull()?.issues ?: listOf()
        for (result in results.values) {
            commonResults = commonResults.filter { result.issues.contains(it) }
        }

        return FindSlotResponse(slot = results.mapValues {
            // subtract commonResults
            CheckResults(issues = it.value.issues.filterNot { commonResults.contains(it) })
        })
    }

    private fun removeCourseFromSchedule(raw: StudentProfile, courseCode: String): StudentProfile {
        return raw.copy(
            schedule = raw.schedule!!.copy(
                terms = raw.schedule.terms.map {
                    it.copy(courseCodes = it.courseCodes.filterNot { code -> code == courseCode })
                }
            )
        )
    }

    private fun mergeSchedule(imported: Schedule, template: Schedule): Schedule {
        return Schedule(terms = template.terms.map { templateTerm ->
            imported.terms.find { it.termName == templateTerm.termName } ?: templateTerm
        })
    }
}
