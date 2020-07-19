package com.watcourses.wat_courses.scraping

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.watcourses.wat_courses.AppProperties
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.persistence.DbCourseSchedule
import com.watcourses.wat_courses.persistence.DbCourseScheduleRepo
import com.watcourses.wat_courses.proto.CourseScheduleResponse
import com.watcourses.wat_courses.proto.Term
import com.watcourses.wat_courses.utils.getCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL


@Service
class ApiScheduleService(
    private val dbCourseScheduleRepo: DbCourseScheduleRepo,
    private val dbCourseRepo: DbCourseRepo,
    private val appProperties: AppProperties
) {
    private val logger: Logger = LoggerFactory.getLogger(ApiScheduleService::class.java)
    private val jsonParser = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    // Update all courses
    @Scheduled(cron = "0 0 2 * * *")
    fun run() {
        logger.info("Start running ApiScheduleService")
        val results = dbCourseRepo.findAll().map {
            Thread.sleep(1000)
            updateCourse(it!!.code, Term.WINTER.getCode(2020))
        }
        logger.info("Finished. ${results.count { !it }} failed out of ${results.size}")
    }

    fun updateCourse(courseCode: String, sessionId: Int): Boolean {
        val courseParts = courseCode.split(" ").takeUnless { it.size != 2 }
            ?: throw IllegalArgumentException("Course code $courseCode not valid")

        val key = appProperties.uwaterloo_open_data_api_key
        val url = "https://api.uwaterloo.ca/v2/courses/${courseParts[0]}/${courseParts[1]}/schedule.json" +
                "?key=$key&term=$sessionId"

        val schedule = scrapeSchedulePageSafe(courseCode, sessionId.toString(), URL(url).openStream())
        if (schedule == null) {
            logger.warn("Failed to scrap $courseCode")
            return false
        }
        return true
    }

    fun scrapeSchedulePageSafe(courseCode: String, termId: String, inputStream: InputStream): DbCourseSchedule? = try {
        scrapeSchedulePage(courseCode, termId, inputStream)
    } catch (e: Exception) {
        logger.warn("Exception $e occurred when trying to parse $courseCode")
        e.printStackTrace()
        null
    }

    // Extract course info from a course schedule.
    // e.g. http://www.adm.uwaterloo.ca/cgi-bin/cgiwrap/infocour/salook.pl?level=under&sess=1205&subject=EMLS&cournum=129R
    fun scrapeSchedulePage(courseCode: String, termId: String, inputStream: InputStream): DbCourseSchedule {
        val res = jsonParser.fromJson<CourseScheduleResponse>(
            InputStreamReader(inputStream),
            CourseScheduleResponse::class.java
        ).data

        val dbSchedule = DbCourseSchedule(
            code = courseCode, termId = termId, sections = res,
            enrolledCap = res.sumBy { sec ->
                sec.enrollmentCapacity.takeIf { sec.section?.startsWith("LEC") == true } ?: 0
            },
            enrolledTotal = res.sumBy { sec ->
                sec.enrollmentTotal.takeIf { sec.section?.startsWith("LEC") == true } ?: 0
            }
        )

        val existing = dbCourseScheduleRepo.findByCodeAndTermId(courseCode, termId)
        if (existing != null) {
            dbCourseScheduleRepo.save(dbSchedule.copy(id = existing.id))
        } else {
            dbCourseScheduleRepo.save(dbSchedule)
        }
        return dbSchedule
    }
}