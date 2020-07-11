package com.watcourses.wat_courses.scraping

import com.watcourses.wat_courses.proto.ReParseConditionsResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ScrapingApi(private val scrapingCourseService: ScrapingCourseService,
                  private val scrapingScheduleService: ScrapingScheduleService) {
    @GetMapping("/scraping/start")
    fun startScraping() {
        scrapingCourseService.updateCourses()
    }

    @GetMapping("/scraping/start-schedule")
    fun startScheduleScraping() {
        scrapingScheduleService.run()
    }

    @GetMapping("/scraping/reparse")
    fun retryParse(@RequestParam("dry_run", defaultValue = "true") dryRun: Boolean): ReParseConditionsResponse {
        return scrapingCourseService.reParseConditions(dryRun)
    }
}