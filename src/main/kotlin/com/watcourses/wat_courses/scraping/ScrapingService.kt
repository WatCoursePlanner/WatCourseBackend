package com.watcourses.wat_courses.scraping

import Term
import com.watcourses.wat_courses.persistence.DbCourse
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbRule
import com.watcourses.wat_courses.persistence.DbRuleRepo
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ScrapingService {
    @Autowired
    private lateinit var dbCourseRepo: DbCourseRepo

    @Autowired
    private lateinit var dbRuleRepo: DbRuleRepo

    private val logger: Logger = LoggerFactory.getLogger(ScrapingService::class.java)

    fun fromUrl(url: String): Document? {
        return try {
            Jsoup.connect(url).get()
        } catch (e: HttpStatusException) {
            logger.warn("Failed to open $url: $e")
            null
        }
    }

    private fun extractTermInfoFromDescription(desc: String): List<Term> {
        val beginningText = "[Offered: "
        val start = desc.lastIndexOf(beginningText)
        val end = desc.lastIndexOf("]")
        if (end <= start) return emptyList()
        val strTerms = desc.substring(start + beginningText.length, end)
        return strTerms.split(",").mapNotNull {
            when (it) {
                "F" -> Term.FALL
                "W" -> Term.WINTER
                "S" -> Term.SPRING
                else -> null
            }
        }
    }

    fun updateCourses() {
        for (courseList in LIST_OF_COURSES_LIST) {
            logger.info("Scrapping $courseList")
            val courses = fromUrl("http://www.ucalendar.uwaterloo.ca/2021/COURSE/course-$courseList.html")
                ?.let { doc -> scrapeCoursePage(doc) }
            if (courses == null) {
                logger.error("Failed to scrap $courseList")
                continue
            }
            logger.info("Done. ${courses.size} courses obtained.")
            for (course in courses) persistCourse(course)
            logger.info("Courses persisted.")
        }
    }

    fun persistCourse(course: DbCourse) {
        course.preRequisite?.let { dbRuleRepo.save(it) }
        course.coRequisite?.let { dbRuleRepo.save(it) }
        course.antiRequisite?.let { dbRuleRepo.save(it) }

        val existing = dbCourseRepo.findByCode(course.code)
        if (existing != null) {
            dbCourseRepo.save(course.copy(id = existing.id))
        } else {
            dbCourseRepo.save(course)
        }
    }

    // Extract course info from a course page. e.g. http://www.ucalendar.uwaterloo.ca/2021/COURSE/course-CS.html
    fun scrapeCoursePage(doc: Document): List<DbCourse> {
        return doc.select("center> table > tbody").map { courseElement ->
            val texts = courseElement.select("tr > td").filterNot { it.text().isEmpty() }
            val basicInfo = texts.filterNot { it.children().firstOrNull()?.tagName() == "i" }.map { it.text() }
            val noteInfo = texts.filter { it.children().firstOrNull()?.tagName() == "i" }.map { it.text().trim() }
            val codeAndCredit = basicInfo[0].split(" ")

            DbCourse(
                name = basicInfo[2],
                code = codeAndCredit.subList(0, 2).joinToString(" "),
                offeringTerms = extractTermInfoFromDescription(basicInfo[3]),
                description = basicInfo[3],
                antiRequisite = noteInfo.find { it.startsWith("Antireq:") }
                    ?.let { DbRule.findOrParse(it, dbRuleRepo) },
                preRequisite = noteInfo.find { it.startsWith("Prereq:") }
                    ?.let { DbRule.findOrParse(it, dbRuleRepo) },
                coRequisite = noteInfo.find { it.startsWith("Coreq:") }
                    ?.let { DbRule.findOrParse(it, dbRuleRepo) },
                courseId = basicInfo[1].let { it.substring(it.lastIndexOf(" ") + 1).trim() }
            )
        }
    }

    companion object {
        val LIST_OF_COURSES_LIST = arrayOf("CS", "PMATH", "GEOE", "ACTSC", "ECE", "MTHEL", "SE")
    }
}