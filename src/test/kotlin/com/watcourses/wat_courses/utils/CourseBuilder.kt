package com.watcourses.wat_courses.utils

import com.watcourses.wat_courses.persistence.DbCourse
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.proto.Term
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

class CourseBuilder(private val dbCourseRepo: DbCourseRepo, private val cachedData: CachedData) {
    var code: String? = null
    var name: String = "Test course"
    var description: String = "Default description"
    var liked: Double? = null
    var easy: Double? = null
    var useful: Double? = null
    var ratingsCount: Int? = null
    var commentsCount: Int? = null
    var offeringTerms = listOf<Term>()

    fun code(code: String) = this.apply { this.code = code }
    fun name(name: String) = this.apply { this.name = name }
    fun description(description: String) = this.apply { this.description = description }
    fun liked(liked: Double?) = this.apply { this.liked = liked }
    fun easy(easy: Double?) = this.apply { this.easy = easy }
    fun useful(useful: Double?) = this.apply { this.useful = useful }
    fun ratingsCount(ratingsCount: Int?) = this.apply { this.ratingsCount = ratingsCount }
    fun commentsCount(commentsCount: Int?) = this.apply { this.commentsCount = commentsCount }
    fun offeringTerms(terms: List<Term>) = this.apply { offeringTerms = terms }

    fun build(): DbCourse {
        val code = code ?: throw AssertionError("Can't create a course without a code")
        dbCourseRepo.save(DbCourse(
            name = name,
            code = code,
            description = description,
            offeringTerms = null,
            preRequisite = null,
            antiRequisite = null,
            coRequisite = null,
            filledCount = ratingsCount,
            commentCount = commentsCount,
            liked = liked,
            easy = easy,
            useful = useful,
            courseId = code,
        ))

        cachedData.invalidateAllCourses()
        return dbCourseRepo.findByCode(code)!!
    }
}

@Component
class CourseBuilderProvider(
    private val dbCourseRepo: DbCourseRepo,
    private val cachedData: CachedData
) {
    fun get(): CourseBuilder = CourseBuilder(dbCourseRepo, cachedData)
}
