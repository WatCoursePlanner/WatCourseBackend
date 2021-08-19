package com.watcourses.wat_courses.utils

import com.watcourses.wat_courses.api.EventApi
import com.watcourses.wat_courses.api.StudentProfileApi
import com.watcourses.wat_courses.api.UserApi
import com.watcourses.wat_courses.proto.*
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.stereotype.Component
import javax.servlet.http.Cookie

class UserSession(
    private val userApi: UserApi,
    private val eventApi: EventApi,
    private val studentProfileApi: StudentProfileApi,
    private val cookies: Array<Cookie>
) {
    private val httpRequest: MockHttpServletRequest
        get() {
            val req = MockHttpServletRequest()
            req.setCookies(*cookies)
            return req
        }

    fun logout() = userApi.logout(httpRequest)

    fun event(e: EventRequest) = eventApi.event(e, httpRequest)

    fun createDefaultStudentProfile(request: CreateDefaultStudentProfileRequest) =
        studentProfileApi.createDefaultStudentProfile(request, httpRequest)

    fun createOrUpdateStudentProfile(request: StudentProfile) =
        studentProfileApi.createOrUpdateStudentProfile(request, httpRequest)
}

@Component
class UserSessionFactory(
    private val userApi: UserApi,
    private val eventApi: EventApi,
    private val studentProfileApi: StudentProfileApi,
) {
    fun register(
        firstName: String = "John",
        lastName: String = "Doe",
        email: String = "test@watcourses.com",
        password: String = "default"
    ): UserSession {
        val resp = MockHttpServletResponse()
        userApi.register(RegisterRequest(firstName, lastName, email, password), resp)
        return UserSession(userApi, eventApi, studentProfileApi, resp.cookies)
    }

    fun login(name: String, password: String = "default"): UserSession {
        val resp = MockHttpServletResponse()
        userApi.login(LoginRequest(name, password), resp)
        return UserSession(userApi, eventApi, studentProfileApi, resp.cookies)
    }

    fun guest(): UserSession = UserSession(userApi, eventApi, studentProfileApi, emptyArray())
}