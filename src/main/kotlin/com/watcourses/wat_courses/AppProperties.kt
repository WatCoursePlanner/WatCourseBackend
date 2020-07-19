package com.watcourses.wat_courses

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("app")
class AppProperties {
    lateinit var google_client_id: String
    lateinit var uwaterloo_open_data_api_key: String // https://uwaterloo.ca/api/
}
