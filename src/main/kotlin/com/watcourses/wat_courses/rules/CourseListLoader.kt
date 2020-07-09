package com.watcourses.wat_courses.rules

import com.watcourses.wat_courses.scraping.ScrapingService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.tomlj.Toml
import org.tomlj.TomlArray

@Service
class CourseListLoader {
    private val courseList = mutableMapOf<String, Set<String>>()
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
                    courseList[list.name] =
                        list.courses + (list.includes.map { courseList[it]!! }.takeIf { it.isNotEmpty() }
                            ?.reduce { a, b -> a + b } ?: emptySet())
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

    fun listContainsCourse(listName: String, courseName: String) =
        (courseList[listName] ?: throw RuntimeException("List $listName does not exist")).contains(courseName)
}