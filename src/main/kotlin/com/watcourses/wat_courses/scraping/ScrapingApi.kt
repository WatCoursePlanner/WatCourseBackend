package com.watcourses.wat_courses.scraping

import ReParseConditionsResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ScrapingApi {
    @Autowired
    private lateinit var scrapingService: ScrapingService

    @GetMapping("/scraping/start")
    fun startScraping() {
        scrapingService.updateCourses()
    }

    @GetMapping("/scraping/reparse")
    fun retryParse() : ReParseConditionsResponse {
        return scrapingService.reParseConditions()
    }
}