package com.watcourses.wat_courses.scraping

import com.watcourses.wat_courses.persistence.DbCourse
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbRule
import com.watcourses.wat_courses.persistence.DbRuleRepo
import com.watcourses.wat_courses.proto.ReParseConditionsResponse
import com.watcourses.wat_courses.proto.ReParseRegressionTestResponse
import com.watcourses.wat_courses.proto.Term
import com.watcourses.wat_courses.rules.RawConditionParser
import com.watcourses.wat_courses.utils.JsoupSafeOpenUrl
import org.jsoup.nodes.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ScrapingCourseService(
    private val dbCourseRepo: DbCourseRepo,
    private val dbRuleRepo: DbRuleRepo,
    private val rawConditionParser: RawConditionParser
) {
    private val logger: Logger = LoggerFactory.getLogger(ScrapingCourseService::class.java)

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

    fun updateCourses(listOfCourseList: Array<String> = LIST_OF_COURSES_LIST) {
        for (courseList in listOfCourseList) {
            logger.info("Scraping $courseList")
            val courses = try {
                JsoupSafeOpenUrl("http://www.ucalendar.uwaterloo.ca/2021/COURSE/course-$courseList.html")
                    ?.let { doc -> scrapeCoursePage(doc) }
            } catch (e: Exception) {
                logger.error("Exception occurred while trying to scrap $courseList: $e")
                null
            }
            if (courses == null) {
                logger.error("Failed to scrap $courseList")
                continue
            }
            logger.info("Done. ${courses.size} courses obtained.")
            for (course in courses) persistCourse(course)
            logger.info("Courses persisted.")
            Thread.sleep(1000)
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
                    ?.let { DbRule.findOrParse(it, dbRuleRepo) { rawConditionParser.parse(it) } },
                preRequisite = noteInfo.find { it.startsWith("Prereq:") }
                    ?.let { DbRule.findOrParse(it, dbRuleRepo) { rawConditionParser.parse(it) } },
                coRequisite = noteInfo.find { it.startsWith("Coreq:") }
                    ?.let { DbRule.findOrParse(it, dbRuleRepo) { rawConditionParser.parse(it) } },
                courseId = basicInfo[1].let { it.substring(it.lastIndexOf(" ") + 1).trim() }
            )
        }
    }

    fun reParseConditions(dryRun: Boolean, parseAll: Boolean = false): ReParseConditionsResponse {
        val rulesToReparse = if (parseAll) dbRuleRepo.findAll().filterNotNull() else dbRuleRepo.findAllByCondIsNull()
        val succeedResults = mutableMapOf<String, String>()
        val failedResults = mutableMapOf<String, String>()
        var successCount = 0
        for (rule in rulesToReparse) {
            val newRule = DbRule.parse(rule.rawRule!!) { rawConditionParser.parse(it) }
            if (rule.parseFailureBecause != newRule.parseFailureBecause || rule.cond != newRule.cond) {
                rule.parseFailureBecause = newRule.parseFailureBecause
                rule.cond = newRule.cond
                if (!dryRun) dbRuleRepo.save(rule)
            }
            if (rule.cond != null) {
                successCount += 1
                succeedResults[rule.rawRule!!] = rule.cond.toString()
            } else {
                failedResults[rule.rawRule!!] = rule.parseFailureBecause.toString()
            }
        }
        return ReParseConditionsResponse(
            total = rulesToReparse.size,
            success = successCount,
            succeedResults = succeedResults,
            failedResults = failedResults,
            dryRun = dryRun
        )
    }

    fun reParseRegressionTest(): ReParseRegressionTestResponse {
        val rulesToReparse = dbRuleRepo.findAllByCondIsNotNull()
        val failedResults = mutableListOf<ReParseRegressionTestResponse.Result>()
        for (rule in rulesToReparse) {
            if (rule.manuallyAssigned) continue
            val newRule = DbRule.parse(rule.rawRule!!) { rawConditionParser.parse(it) }
            if (rule.cond != newRule.cond || rule.fullyResolved != newRule.fullyResolved) {
                failedResults.add(
                    ReParseRegressionTestResponse.Result(
                        rawRule = rule.rawRule!!,
                        old = "${rule.fullyResolved} ${rule.cond?.toString()}",
                        new = "${newRule.fullyResolved} ${newRule.cond?.toString()}",
                        error = newRule.parseFailureBecause
                    )
                )
            }
        }
        return ReParseRegressionTestResponse(
            total = rulesToReparse.size,
            regressionNum = failedResults.size,
            results = failedResults
        )
    }

    companion object {
        val LIST_OF_COURSES_LIST = arrayOf(
            "ACTSC", "AE", "AFM", "AHS", "AMATH",
            "ANTH", "APPLS", "ARABIC", "ARBUS", "ARCH",
            "ARTS", "ASL", "AVIA", "BASE", "BET", "BIOL",
            "BME", "BUS", "CDNST", "CHE", "CHEM", "CHINA",
            "CI", "CIVE", "CLAS", "CMW", "CO",
            "COGSCI", "COMM", "COOP", "CROAT", "CS",
            "DAC", "DUTCH", "EARTH", "EASIA", "ECE",
            "ECON", "EMLS", "ENBUS", "ENGL", "ENVE",
            "ENVS", "ERS", "FINE", "FR", "GBDA",
            "GENE", "GEOE", "GEOG", "GER", "GERON",
            "GRK", "GSJ", "HIST", "HLTH", "HRM",
            "HRTS", "HUMSC", "INDEV", "INDG", "INTEG",
            "INTST", "ITAL", "ITALST", "JAPAN", "JS",
            "KIN", "KOREA", "LAT", "LS", "MATBUS",
            "MATH", "ME", "MEDVL", "MENN", "MGMT",
            "MNS", "MOHAWK", "MSCI", "MTE", "MTHEL",
            "MUSIC", "NE", "OPTOM", "PACS", "PD",
            "PDARCH", "PDPHRM", "PHARM", "PHIL", "PHYS",
            "PLAN", "PMATH", "PORT", "PSCI", "PSYCH",
            "REC", "REES", "RS", "RUSS", "SCBUS",
            "SCI", "SDS", "SE", "SI", "SMF",
            "SOC", "SOCWK", "SPAN", "SPCOM", "STAT",
            "STV", "SVENT", "SWREN", "SYDE", "THPERF",
            "UNIV", "VCULT", "WKRPT"
        )
    }
}