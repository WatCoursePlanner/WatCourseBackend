package com.watcourses.wat_courses

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class WatCoursesApplication {
    @Bean
    fun corsConfigurer(): WebMvcConfigurer? {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:3000", "https://watcourses.com")
                    .allowedMethods("*")
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<WatCoursesApplication>(*args)
}
