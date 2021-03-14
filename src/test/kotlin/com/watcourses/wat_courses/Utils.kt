package com.watcourses.wat_courses

import com.watcourses.wat_courses.persistence.DbCourse
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbUser
import com.watcourses.wat_courses.persistence.DbUserRepo
import org.springframework.stereotype.Component

@Component
class Utils(
    private val dbCourseRepo: DbCourseRepo,
    private val dbUserRepo: DbUserRepo,
) {
    fun createCourse(vararg code: String) =
        code.forEach {
            if (dbCourseRepo.findByCode(it) == null) {
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
        }

    fun createCourses(codes: List<String>) = createCourse(*codes.toTypedArray())

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

    fun createUserWithEmail(email: String) =
        dbUserRepo.save(
            DbUser(
                email = email,
                firstName = "",
                lastName = "",
                password = "",
                sessionId = "",
                studentProfile = null,
            )
        )
}