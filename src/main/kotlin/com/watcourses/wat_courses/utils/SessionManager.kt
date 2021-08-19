package com.watcourses.wat_courses.utils

import com.watcourses.wat_courses.persistence.DbUser
import com.watcourses.wat_courses.persistence.DbUserSession
import com.watcourses.wat_courses.persistence.DbUserSessionRepo
import net.bytebuddy.utility.RandomString
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private const val LENGTH = 20
private const val SESSION_COOKIE_NAME = "watcourses_session"

@Component
class SessionManager(
    private val dbUserSessionRepo: DbUserSessionRepo,
) {
    private val randomString = RandomString(LENGTH)

    fun generateSessionToken(): String = randomString.nextString()

    fun generateGuestSessionToken(): String = "g_" + randomString.nextString()

    fun generateAndSendSessionId(response: HttpServletResponse, dbUser: DbUser) {
        val expireDate = Calendar.getInstance()
        expireDate.add(Calendar.DATE, 30)
        val sessionToken = generateSessionToken()
        dbUserSessionRepo.save(
            DbUserSession(
                sessionToken = sessionToken,
                user = dbUser,
                expiresAt = expireDate.time,
            )
        )
        val cookie = Cookie(SESSION_COOKIE_NAME, sessionToken)
        cookie.isHttpOnly = true
        cookie.path = "/"
        cookie.secure = true
        cookie.maxAge = 60 * 60 * 24 * 30 // 30 days
        response.addCookie(cookie)
    }

    fun getSessionToken(request: HttpServletRequest): String? {
        return request.cookies?.findLast { it.name == SESSION_COOKIE_NAME }?.value
    }

    fun getCurrentSession(request: HttpServletRequest): DbUserSession? {
        return getSessionToken(request)?.let { dbUserSessionRepo.findBySessionToken(it) }
    }

    fun getCurrentUser(request: HttpServletRequest): DbUser? {
        return getCurrentSession(request)?.user
    }
}
