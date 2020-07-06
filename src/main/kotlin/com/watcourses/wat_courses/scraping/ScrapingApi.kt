package com.watcourses.wat_courses.scraping

import CourseInfo
import Term
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ScrapingApi {
    @GetMapping("/scraping/start")
    fun startScraping() {

    }
}