package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.proto.ReParseConditionsResponse
import com.watcourses.wat_courses.proto.ReParseRegressionTestResponse
import com.watcourses.wat_courses.proto.RuleImportRequest
import com.watcourses.wat_courses.proto.RuleImportResponse
import com.watcourses.wat_courses.rules.RuleImporter
import com.watcourses.wat_courses.scraping.ApiScheduleService
import com.watcourses.wat_courses.scraping.ScrapingCourseService
import com.watcourses.wat_courses.scraping.UwFlowScrapingService
import org.springframework.web.bind.annotation.*

@RestController
class ScrapingApiAdmin(
    private val scrapingCourseService: ScrapingCourseService,
    private val apiScheduleService: ApiScheduleService,
    private val uwFlowScrapingService: UwFlowScrapingService,
    private val ruleImporter: RuleImporter
) {
    @GetMapping("/admin/scraping/start-courses")
    fun startScraping() {
        scrapingCourseService.updateCourses()
    }

    @GetMapping("/admin/scraping/start-schedule")
    fun startScheduleScraping() {
        apiScheduleService.run()
    }

    @GetMapping("/admin/scraping/start-uwflow")
    fun startUwFlowScraping() {
        uwFlowScrapingService.run()
    }

    @PostMapping("/admin/rules/import")
    fun importRules(@RequestBody request: RuleImportRequest): RuleImportResponse {
        return ruleImporter.import(request)
    }

    // Test and apply your new parser to existing rules in the database
    @GetMapping("/admin/rules/reparse")
    fun retryParse(
        @RequestParam("dry_run", defaultValue = "true") dryRun: Boolean,
        @RequestParam("parse_all", defaultValue = "false") parseAll: Boolean
    ): ReParseConditionsResponse {
        return scrapingCourseService.reParseConditions(dryRun, parseAll)
    }

    // Test if your changes to parser would affect any existing parsed rules.
    @GetMapping("/admin/rules/reparse_regression")
    fun retryParseRegressionTest(): ReParseRegressionTestResponse {
        return scrapingCourseService.reParseRegressionTest()
    }
}
