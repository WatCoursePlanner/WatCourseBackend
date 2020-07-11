package com.watcourses.wat_courses.utils

import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory

fun JsoupSafeOpenUrl(url: String): Document? {
    return try {
        Jsoup.connect(url).get()
    } catch (e: HttpStatusException) {
        LoggerFactory.getLogger(Jsoup::class.java).warn("Failed to open $url: $e")
        null
    }
}