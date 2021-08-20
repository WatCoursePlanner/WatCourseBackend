package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbStudentProfile
import com.watcourses.wat_courses.persistence.DbStudentProfileRepo
import com.watcourses.wat_courses.persistence.DbStudentProfileSchedule
import com.watcourses.wat_courses.persistence.DbStudentProfileScheduleRepo
import com.watcourses.wat_courses.persistence.DbTermScheduleRepo
import com.watcourses.wat_courses.persistence.DbUserRepo
import com.watcourses.wat_courses.proto.*
import com.watcourses.wat_courses.rules.Checker
import com.watcourses.wat_courses.rules.DegreeRequirementLoader
import com.watcourses.wat_courses.utils.SessionManager
import com.watcourses.wat_courses.utils.create
import com.watcourses.wat_courses.utils.unionFlatten
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@Transactional
@RestController
class StudentProfileApi(
    private val degreeRequirementLoader: DegreeRequirementLoader,
    private val dbStudentProfileScheduleRepo: DbStudentProfileScheduleRepo,
    private val dbStudentProfileRepo: DbStudentProfileRepo,
    private val dbTermScheduleRepo: DbTermScheduleRepo,
    private val dbCourseRepo: DbCourseRepo,
    private val dbUserRepo: DbUserRepo,
    private val checker: Checker,
    private val sessionManager: SessionManager,
) {
    @GetMapping("/profile/default/{program}")
    fun getDefaultStudentProfile(@PathVariable program: String): StudentProfile {
        val degreeRequirement = degreeRequirementLoader.getDegreeRequirement(program)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found")
        val schedule = degreeRequirement.defaultSchedule
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Default schedule unavailable for program")
        return StudentProfile(
            schedule = Schedule.create(schedule, Calendar.getInstance().get(Calendar.YEAR), CoopStream.NO_COOP),
            degrees = listOf(program),
            labels = degreeRequirement.labels.toList()
        )
    }

    @PostMapping("/profile/create")
    fun createDefaultStudentProfile(
        @RequestBody request: CreateDefaultStudentProfileRequest,
        httpRequest: HttpServletRequest
    ): StudentProfile {
        val degrees = request.degrees
        val degreeRequirements = degrees.map { degreeRequirementLoader.getDegreeRequirement(it)!! }
        val defaultSchedule = degreeRequirements
            .single { it.defaultSchedule?.terms?.isNotEmpty() == true }
            .defaultSchedule!!
        val schedule = Schedule.create(defaultSchedule, request.startingYear!!, request.coopStream!!)
        val labels = degreeRequirements.map { it.labels.toSet() }.unionFlatten().toMutableList()
        val profile = StudentProfile(schedule = schedule, labels = labels, degrees = degrees)
        // do not create db entity for guest users
        val owner = sessionManager.getCurrentUser(httpRequest) ?: return profile

        val dbStudentProfile = DbStudentProfile.createOrUpdate(
            dbStudentProfileRepo = dbStudentProfileRepo,
            dbStudentProfileScheduleRepo = dbStudentProfileScheduleRepo,
            dbTermScheduleRepo = dbTermScheduleRepo,
            dbCourseRepo = dbCourseRepo,
            studentProfile = profile,
            owner = owner,
        )
        owner.studentProfile = dbStudentProfile
        dbUserRepo.save(owner)
        return dbStudentProfile.toProto()
    }

    @PostMapping("/profile/create-or-update")
    fun createOrUpdateStudentProfile(
        @RequestBody studentProfile: StudentProfile,
        httpRequest: HttpServletRequest
    ): StudentProfile {
        val owner = sessionManager.getCurrentUser(httpRequest)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Not logged in")

        val dbStudentProfile = DbStudentProfile.createOrUpdate(
            dbStudentProfileScheduleRepo = dbStudentProfileScheduleRepo,
            dbTermScheduleRepo = dbTermScheduleRepo,
            dbStudentProfileRepo = dbStudentProfileRepo,
            dbCourseRepo = dbCourseRepo,
            studentProfile = studentProfile,
            owner = owner,
        )
        owner.studentProfile = dbStudentProfile
        dbUserRepo.save(owner)
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
}
