package com.watcourses.wat_courses.rules

import Schedule

data class Requirement(
    val name: String,
    val condition: Condition
)

data class DegreeRequirement(
    val name: String,
    val source: String,
    val requirements: List<Requirement>,
    val defaultSchedule: Schedule?,
    val labels: Set<String>
)