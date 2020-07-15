package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.proto.*
import com.watcourses.wat_courses.rules.Checker
import com.watcourses.wat_courses.rules.DegreeRequirementLoader
import com.watcourses.wat_courses.utils.create
import com.watcourses.wat_courses.utils.unionFlatten
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class StudentProfileApi(
    private val degreeRequirementLoader: DegreeRequirementLoader,
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
        return StudentProfile(
            schedule = Schedule.create(defaultSchedule, startingYear, stream),
            degrees = degrees,
            labels = degreeRequirements.map { it.labels.toSet() }.unionFlatten().toList()
        )
    }

    @PostMapping("/profile/check")
    fun checkProfile(@RequestBody profile: StudentProfile): CheckResults {
        return checker.check(profile)
    }

    @PostMapping("/profile/find_slots")
    fun findSlots(@RequestBody request: FindSlotRequest): FindSlotResponse {
        val profile = request.profile!!
        val course = request.courseCode!!

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
}
