package com.watcourses.wat_courses.utils

import net.bytebuddy.utility.RandomString
import org.springframework.stereotype.Component

private const val LENGTH = 20

@Component
class SessionManager {
    private val randomString = RandomString(LENGTH)
    fun generateSessionId(): String = randomString.nextString()
}
