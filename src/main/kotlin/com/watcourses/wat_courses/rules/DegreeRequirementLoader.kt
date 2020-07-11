package com.watcourses.wat_courses.rules

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.watcourses.wat_courses.proto.Schedule
import com.watcourses.wat_courses.scraping.ScrapingCourseService
import com.watcourses.wat_courses.utils.ClassPathResourceReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.tomlj.Toml
import org.tomlj.TomlArray
import org.tomlj.TomlTable

@Service
class DegreeRequirementLoader(private val resourceReader: ClassPathResourceReader) {
    private lateinit var degrees: Map<String, DegreeRequirement> // name -> DegreeRequirement
    private val logger: Logger = LoggerFactory.getLogger(ScrapingCourseService::class.java)

    init {
        try {
            loadDegreeRequirements()
        } catch (e: Exception) {
            logger.warn("Failed to load lists because $e")
        }
    }

    final fun loadDegreeRequirements() {
        val degreeList = resourceReader.get("degrees").file.listFiles()?.filterNotNull()?.map { file ->
            val degreeFile = Toml.parse(file.inputStream())
            DegreeRequirement(
                name = degreeFile["name"]?.toString() ?: file.nameWithoutExtension,
                source = degreeFile["source"]?.toString() ?: "",
                requirements = (degreeFile["requirements"] as TomlArray?)?.toList()?.map { it as TomlTable }
                    ?.map {
                        Requirement(
                            name = it["name"] as String,
                            condition = ConditionParser.parseToEnd(it["cond"] as String)
                        )
                    } ?: listOf(),
                labels = (degreeFile["labels"] as TomlArray?)?.toList()?.map { it as String }?.toSet() ?: emptySet(),
                defaultSchedule = (degreeFile["default_schedule.term"] as TomlArray?)?.toList()?.map { it as TomlTable }
                    ?.let {
                        Schedule(terms = it.map { term ->
                            Schedule.TermSchedule(
                                termName = term["name"] as String,
                                courseCodes = (term["courses"] as TomlArray?)?.toList()?.map { it as String }
                                    ?: emptyList()
                            )
                        })
                    }
            )
        }?.toList()
        degrees = degreeList?.associateBy { it.name } ?: emptyMap()
        logger.info("${degrees.keys.size} degree files loaded")
    }

    fun getDegreeRequirement(name: String) = degrees[name]
}
