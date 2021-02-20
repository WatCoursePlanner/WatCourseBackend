package com.watcourses.wat_courses

import com.watcourses.wat_courses.persistence.DbCourse
import com.watcourses.wat_courses.persistence.DbCourseRepo
import org.springframework.stereotype.Component

@Component
class Utils(private val dbCourseRepo: DbCourseRepo) {
    fun createCourse(vararg code: String) =
        code.forEach {
            dbCourseRepo.save(
                DbCourse(
                    name = "$it name",
                    code = it,
                    antiRequisite = null,
                    coRequisite = null,
                    description = "",
                    offeringTerms = null,
                    preRequisite = null,
                    courseId = it
                )
            )
        }

    fun createSingleCourse(code: String, name: String) =
        dbCourseRepo.save(
            DbCourse(
                name = name,
                code = code,
                antiRequisite = null,
                coRequisite = null,
                description = "",
                offeringTerms = null,
                preRequisite = null,
                courseId = code
            )
        )
}