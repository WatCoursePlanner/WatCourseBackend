package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.proto.ReParseConditionsResponse
import com.watcourses.wat_courses.scraping.ScrapingCourseService
import com.watcourses.wat_courses.scraping.ScrapingScheduleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ScrapingApiAdmin(private val scrapingCourseService: ScrapingCourseService,
                       private val scrapingScheduleService: ScrapingScheduleService
) {
    @GetMapping("/admin/scraping/start-courses")
    fun startScraping() {
        scrapingCourseService.updateCourses()
    }

    @GetMapping("/admin/scraping/start-schedule")
    fun startScheduleScraping() {
        scrapingScheduleService.run()
    }

    @GetMapping("/admin/scraping/reparse")
    fun retryParse(@RequestParam("dry_run", defaultValue = "true") dryRun: Boolean): ReParseConditionsResponse {
        return scrapingCourseService.reParseConditions(dryRun)
    }
}