package com.watcourses.wat_courses.rules

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.proto.CourseList
import com.watcourses.wat_courses.utils.ClassPathResourceReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.tomlj.Toml
import org.tomlj.TomlArray

@Component
class CourseListLoader(private val dbCourseRepo: DbCourseRepo, private val resourceReader: ClassPathResourceReader) {
    private val courseList = mutableMapOf<String, CourseList>()
    private val logger: Logger = LoggerFactory.getLogger(CourseListLoader::class.java)

    init {
        try {
            loadLists()
        } catch (e: Exception) {
            logger.warn("Failed to load lists because $e")
        }
    }

    final fun loadLists() {
        data class UnresolvedList(
            val name: String,
            val courses: Set<String>,
            val includes: List<String>,
            val except: Set<String>,
            var added: Boolean = false
        )

        val unresolvedLists = resourceReader.getResources("lists/*").map { res ->
            val listFile = Toml.parse(res.inputStream)
            val courses = listFile["courses"] as TomlArray?
            val includes = listFile["include"] as TomlArray?
            val except = listFile["except"] as TomlArray?
            UnresolvedList(
                name = res.filename!!.substringBefore('.'),
                courses = courses?.toList()?.map { it as String }?.toSet() ?: setOf(),
                includes = includes?.toList()?.map { it as String } ?: listOf(),
                except = except?.toList()?.map { it as String }?.toSet() ?: setOf()
            )
        }.toMutableList()

        while (unresolvedLists.any { !it.added }) {
            var resolvedAtLeastOne = false
            for (list in unresolvedLists) {
                if (list.includes.all { includeName -> unresolvedLists.find { it.name == includeName && it.added } != null }) {
                    val courses =
                        (list.courses + (list.includes.map { courseList[it]!!.courses }).flatten().toSet())
                            .asSequence()
                            .map { lookupCourseWildcard(it) }.flatten().toSet()
                            .filter { course -> list.except.all { !matchCourseWildcard(it, course) } }.toList()

                    courseList[list.name] = CourseList(
                        courses = courses,
                        name = list.name
                    )
                    list.added = true
                    resolvedAtLeastOne = true
                }
            }
            if (!resolvedAtLeastOne) {
                logger.error("Cyclic include or included file not found: ${unresolvedLists.map { it.name }}")
                throw RuntimeException("Failed to read course lists.")
            }
        }
        logger.info("${courseList.keys.size} course lists loaded")
    }

    fun getList(listName: String) =
        (courseList[listName] ?: throw RuntimeException("List $listName does not exist"))

    fun getListOrNull(listName: String) = courseList[listName]

    fun listContainsCourse(listName: String, courseCode: String) =
        getList(listName).courses.contains(courseCode)

    fun matchCourseWildcard(wildcardCourseCode: String, courseCode: String): Boolean {
        if (wildcardCourseCode.contains("*") && wildcardCourseCode.last() != '*') {
            throw IllegalArgumentException("Currently only support code ends with a wildcard: $wildcardCourseCode")
        }
        return courseCode.startsWith(wildcardCourseCode.substringBefore("*"))
    }

    fun lookupCourseWildcard(wildcardCourseCode: String): List<String> {
        if (wildcardCourseCode.contains("*") && wildcardCourseCode.last() != '*') {
            throw IllegalArgumentException("Currently only support code ends with a wildcard: $wildcardCourseCode")
        }
        return dbCourseRepo.findAllByCodeStartingWith(wildcardCourseCode.substringBefore("*")).map { it.code }
            .takeIf { it.isNotEmpty() }
            ?: throw IllegalArgumentException("Course $wildcardCourseCode does not match any results")
    }
}