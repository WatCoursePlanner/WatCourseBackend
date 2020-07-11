package com.watcourses.wat_courses.rules

import com.watcourses.wat_courses.proto.CourseList
import com.watcourses.wat_courses.scraping.ScrapingService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.tomlj.Toml
import org.tomlj.TomlArray

@Service
class CourseListLoader {
    private val courseList = mutableMapOf<String, CourseList>()
    private val logger: Logger = LoggerFactory.getLogger(ScrapingService::class.java)

    init {
        data class UnresolvedList(
            val name: String,
            val courses: Set<String>,
            val includes: List<String>,
            var added: Boolean = false
        )

        val unresolvedLists = ClassPathResource("lists").file.listFiles()?.filterNotNull()?.map { file ->
            val listFile = Toml.parse(file.inputStream())
            val courses = listFile["courses"] as TomlArray?
            val includes = listFile["include"] as TomlArray?
            UnresolvedList(
                name = file.nameWithoutExtension,
                courses = courses?.toList()?.map { it as String }?.toSet() ?: setOf(),
                includes = includes?.toList()?.map { it as String } ?: listOf()
            )
        }?.toMutableList() ?: mutableListOf()
        while (unresolvedLists.filter { !it.added }.isNotEmpty()) {
            var resolvedAtLeastOne = false
            for (list in unresolvedLists) {
                if (list.includes.all { includeName -> unresolvedLists.find { it.name == includeName && it.added } != null }) {
                    courseList[list.name] = CourseList(
                        courses = (list.courses + (list.includes.map { courseList[it]!!.courses }).flatten().toSet()).toList(),
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

    fun listContainsCourse(listName: String, courseName: String) =
        getList(listName).courses.contains(courseName)
}