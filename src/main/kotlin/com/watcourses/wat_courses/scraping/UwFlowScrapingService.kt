package com.watcourses.wat_courses.scraping

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.proto.CoursesResponse
import com.watcourses.wat_courses.proto.UwFlowCourse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private val URL = "https://uwflow.com/graphql"
private val QUERY = """
    query {
      course {
        id
        code
        rating {
          liked
          easy
          useful
          filled_count
          comment_count
        }
      }
    }
""".trimIndent()

@Service
class UwFlowScrapingService(
    private val dbCourseRepo: DbCourseRepo
) {
    private data class GraphQLRequest(val query: String)

    private val logger: Logger = LoggerFactory.getLogger(ApiScheduleService::class.java)
    private val jsonParser = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    // Update ratings data from UWFlow
    @Scheduled(cron = "0 0 2 * * *")
    fun run() {
        logger.info("Start running UwFlowScrapingService")

        val data = parseData(fetchFromUwFlow())

        logger.info("${data.size} courses found")

        val results = data.map { persist(it) }

        logger.info("persisted ${results.count { it }}/${data.size} courses")
    }

    fun persist(course: UwFlowCourse): Boolean {
        val code = convertCode(course.code!!)
        val dbCourse = dbCourseRepo.findByCode(code) ?: return false
        dbCourse.easy = course.rating!!.easy
        dbCourse.liked = course.rating.liked
        dbCourse.useful = course.rating.useful
        dbCourse.commentCount = course.rating.commentCount
        dbCourse.filledCount = course.rating.filledCount
        dbCourseRepo.save(dbCourse)
        return true
    }

    fun parseData(stream: InputStream): List<UwFlowCourse> {
        return jsonParser.fromJson<CoursesResponse>(
            InputStreamReader(stream),
            CoursesResponse::class.java
        ).data?.course ?: listOf()
    }

    // ece140 -> ECE 140
    private fun convertCode(code: String): String {
        val digitStartIndex = code.indexOfFirst { it.isDigit() }
        val upperCode = code.toUpperCase()
        return upperCode.substring(0, digitStartIndex) + " " + upperCode.substring(digitStartIndex)
    }

    private fun fetchFromUwFlow(): InputStream {
        val data = jsonParser.toJson(GraphQLRequest(QUERY))
        val conn = URL(URL).openConnection() as HttpURLConnection
        conn.doOutput = true
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("User-Agent", "WatCourses")
        conn.setRequestProperty("charset", "utf-8")
        conn.setRequestProperty("Content-Length", data.length.toString())
        DataOutputStream(conn.outputStream).use { wr -> wr.write(data.toByteArray()) }
        return conn.inputStream
    }

}