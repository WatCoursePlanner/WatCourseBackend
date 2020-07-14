package com.watcourses.wat_courses.scraping

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbCourseSchedule
import com.watcourses.wat_courses.persistence.DbCourseScheduleRepo
import com.watcourses.wat_courses.proto.CourseSection
import com.watcourses.wat_courses.proto.ReservedEnrolInfo
import com.watcourses.wat_courses.proto.Term
import com.watcourses.wat_courses.utils.JsoupSafeOpenUrl
import com.watcourses.wat_courses.utils.getCode
import org.jsoup.nodes.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
class ScrapingScheduleService(
    private val dbCourseScheduleRepo: DbCourseScheduleRepo,
    private val dbCourseRepo: DbCourseRepo
) {
    private val logger: Logger = LoggerFactory.getLogger(ScrapingScheduleService::class.java)

    // Update all courses
    @Scheduled(cron = "0 0 2 * * *")
    fun run() {
        logger.info("Start running ScrapingScheduleService")
        val results = dbCourseRepo.findAll().map { updateCourse(it!!.code, Term.WINTER.getCode(2020)) }
        logger.info("Finished. ${results.count { !it }} failed out of ${results.size}")
    }

    fun updateCourse(courseCode: String, sessionId: Int): Boolean {
        val courseParts = courseCode.split(" ").takeUnless { it.size != 2 }
            ?: throw IllegalArgumentException("Course code $courseCode not valid")

        val schedule =
            JsoupSafeOpenUrl(
                "http://www.adm.uwaterloo.ca/cgi-bin/cgiwrap/infocour/salook.pl?level=under" +
                        "&sess=$sessionId&subject=${courseParts[0]}&cournum=${courseParts[1]}"
            )?.let { doc -> scrapeSchedulePageSafe(courseCode, doc) }
        if (schedule == null) {
            logger.warn("Failed to scrap $courseCode")
            return false
        }
        return true
    }

    fun scrapeSchedulePageSafe(courseCode: String, doc: Document): DbCourseSchedule? = try {
        scrapeSchedulePage(courseCode, doc)
    } catch (e: Exception) {
        null
    }

    // Extract course info from a course schedule.
    // e.g. http://www.adm.uwaterloo.ca/cgi-bin/cgiwrap/infocour/salook.pl?level=under&sess=1205&subject=EMLS&cournum=129R
    fun scrapeSchedulePage(courseCode: String, doc: Document): DbCourseSchedule {
        data class Appendix(
            val reserve: String? = null, val enrolCap: Int? = null, val enrolTotal: Int? = null, val time: String
        )

        val term = Regex("Term: (\\d+)").find(doc.text())!!.groupValues[1]
        val rows = doc.select("td > table > tbody > tr").mapNotNull { row ->
            val rawRow = row.select("td").map { it.text() }
            try {
                @Suppress("IMPLICIT_CAST_TO_ANY")
                when {
                    rawRow.isEmpty() -> null
                    rawRow[0].toIntOrNull() != null -> CourseSection(
                        sectionId = rawRow[0].toInt(),
                        section = rawRow[1],
                        location = rawRow[2],
                        //assocClass = rawRow[3].toInt(),
                        enrolCap = rawRow[6].toInt(),
                        enrolTotal = rawRow[7].toInt(),
                        time = listOf(rawRow[10]),
                        room = rawRow[11],
                        instructor = rawRow.getOrNull(12)?.split(",")?.reversed()?.joinToString(" "),
                        reservedEnrolInfo = listOf()
                    )
                    rawRow[0].startsWith("Reserve") -> Appendix(
                        reserve = rawRow[0].substringAfter("Reserve:").trim(),
                        enrolCap = rawRow[1].toInt(),
                        enrolTotal = rawRow[2].toInt(),
                        time = rawRow[5]
                    )
                    rawRow.size >= 11 -> Appendix(time = rawRow[10])
                    else -> null
                }
            } catch (e: Exception) {
                logger.warn("Exception occurred when scraping. code=$courseCode, term=$term, row=$rawRow, e=$e")
                throw e
            }
        }
        val sections = mutableListOf<CourseSection>()
        if (rows.isNotEmpty()) {
            var currentSection = rows[0] as CourseSection
            var currentSectionTime = currentSection.time.toMutableList()
            var currentSectionReservedEnrolInfo = currentSection.reservedEnrolInfo.toMutableList()

            for (i in 1 until rows.size) {
                if (rows[i] is CourseSection) {
                    sections.add(
                        currentSection.copy(
                            reservedEnrolInfo = currentSectionReservedEnrolInfo,
                            time = currentSectionTime.filter { it.isNotBlank() }
                        )
                    )
                    currentSection = rows[i] as CourseSection
                    currentSectionTime = currentSection.time.toMutableList()
                    currentSectionReservedEnrolInfo = currentSection.reservedEnrolInfo.toMutableList()
                } else {
                    val appendix = rows[i] as Appendix
                    if (appendix.reserve != null) currentSectionReservedEnrolInfo.add(
                        ReservedEnrolInfo(
                            reserve = appendix.reserve,
                            cap = appendix.enrolCap!!,
                            total = appendix.enrolTotal!!
                        )
                    )
                    currentSectionTime.add(appendix.time)
                }
            }
            sections.add(
                currentSection.copy(
                    reservedEnrolInfo = currentSectionReservedEnrolInfo,
                    time = currentSectionTime.filter { it.isNotBlank() }
                )
            )
        }
        val dbSchedule = DbCourseSchedule(
            code = courseCode, termId = term, sections = sections,
            enrolledCap = sections.sumBy { sec -> sec.enrolCap.takeIf { sec.section!!.startsWith("LEC") } ?: 0 },
            enrolledTotal = sections.sumBy { sec -> sec.enrolTotal.takeIf { sec.section!!.startsWith("LEC") } ?: 0 }
        )

        val existing = dbCourseScheduleRepo.findByCodeAndTermId(courseCode, term)
        if (existing != null) {
            dbCourseScheduleRepo.save(dbSchedule.copy(id = existing.id))
        } else {
            dbCourseScheduleRepo.save(dbSchedule)
        }
        return dbSchedule
    }
}