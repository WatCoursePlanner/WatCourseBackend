package com.watcourses.wat_courses.utils

fun List<Set<String>>.unionFlatten() = takeIf { it.isNotEmpty() }?.reduce { a, b -> a + b } ?: emptySet()
