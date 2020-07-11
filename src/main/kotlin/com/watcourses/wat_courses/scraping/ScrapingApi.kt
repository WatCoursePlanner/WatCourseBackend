package com.watcourses.wat_courses.scraping

import com.watcourses.wat_courses.proto.ReParseConditionsResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ScrapingApi(private val scrapingService: ScrapingService) {
    @GetMapping("/scraping/start")
    fun startScraping() {
        scrapingService.updateCourses()
    }

    @GetMapping("/scraping/reparse")
    fun retryParse(@RequestParam("dry_run", defaultValue = "true") dryRun: Boolean): ReParseConditionsResponse {
        return scrapingService.reParseConditions(dryRun)
    }
}