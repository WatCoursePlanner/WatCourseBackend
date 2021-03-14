package com.watcourses.wat_courses.utils

import com.watcourses.wat_courses.persistence.DbUser
import com.watcourses.wat_courses.persistence.DbUserRepo
import net.bytebuddy.utility.RandomString
import org.springframework.stereotype.Component
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private const val LENGTH = 20
private const val SESSION_COOKIE_NAME = "watcourses_session"

@Component
class SessionManager(private val dbUserRepo: DbUserRepo) {
    private val randomString = RandomString(LENGTH)

    fun generateSessionId(): String = randomString.nextString()

    fun generateGuestSessionId(): String = "g_" + randomString.nextString()

    fun generateAndSendSessionId(response: HttpServletResponse, dbUser: DbUser) {
        dbUser.sessionId = generateSessionId()
        val cookie = Cookie(SESSION_COOKIE_NAME, dbUser.sessionId)
        cookie.isHttpOnly = true
        cookie.path = "/"
        cookie.secure = true
        cookie.maxAge = 60 * 60 * 24 * 30 // 30 days
        response.addCookie(cookie)
    }

    fun getSessionId(request: HttpServletRequest): String? {
        return request.cookies?.findLast { it.name == SESSION_COOKIE_NAME }?.value
    }

    fun getCurrentUser(request: HttpServletRequest): DbUser? {
        return getSessionId(request)?.let { dbUserRepo.findBySessionId(it) }
    }
}
