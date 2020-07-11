package com.watcourses.wat_courses.utils

import com.watcourses.wat_courses.proto.StudentProfile
import com.watcourses.wat_courses.rules.DegreeRequirement
import com.watcourses.wat_courses.rules.TermResolver

fun StudentProfile.getDegreeLabels(degreeRequirements: List<DegreeRequirement>): Set<String> {
    return degreeRequirements.map { it.labels }.unionFlatten()
}

fun StudentProfile.getLabels(degreeLabels: Set<String>, termName: String? = null): Set<String> {
    return labels.toSet() + degreeLabels + (termName?.let { TermResolver.getApplicableLabels(termName) } ?: setOf())
}