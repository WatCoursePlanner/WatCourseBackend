package com.watcourses.wat_courses.utils

import com.watcourses.wat_courses.proto.StudentProfile
import com.watcourses.wat_courses.rules.DegreeRequirement
import com.watcourses.wat_courses.rules.TermResolver

fun StudentProfile.getDegreeLabels(degreeRequirements: List<DegreeRequirement>): Set<String> {
    return degreeRequirements.map { it.labels }.unionFlatten()
}

fun StudentProfile.getLabels(degreeLabels: Set<String>, termName: String? = null): Set<String> {
    // If it is a work term, use the last academic term
    if (termName?.startsWith("WT") == true) {
        val terms = schedule!!.terms
        val index = terms.indexOf(terms.find{it.termName == termName })
        val lastTerm = terms.subList(0, index).findLast { !it.termName!!.startsWith("WT") }!!
        return getLabels(degreeLabels, lastTerm.termName)
    }
    return labels.toSet() + degreeLabels + (termName?.let { TermResolver.getApplicableLabels(termName) } ?: setOf())
}
