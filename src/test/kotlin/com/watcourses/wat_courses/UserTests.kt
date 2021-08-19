package com.watcourses.wat_courses

import com.watcourses.wat_courses.api.UserApi
import com.watcourses.wat_courses.proto.LoginRequest
import com.watcourses.wat_courses.proto.RegisterRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserTests {
    @Autowired
    private lateinit var userApi: UserApi

    @Test
    fun `can register and login`() {
        val resp = MockHttpServletResponse()
        assertThat(userApi.register(RegisterRequest(), resp).success).isEqualTo(false)
        assertThat(resp.cookies.size).isEqualTo(0)
        val result =
            userApi.register(
                RegisterRequest(
                    firstName = "first",
                    lastName = "last",
                    email = "test@example.com",
                    password = "very_secure_password"
                ), resp
            )
        assertThat(result.success).isEqualTo(true)
        with(result.userInfo!!) {
            assertThat(firstName).isEqualTo("first")
            assertThat(lastName).isEqualTo("last")
            assertThat(email).isEqualTo("test@example.com")
        }
        assertThat(resp.cookies[0].name).isEqualTo("watcourses_session")
        val sessionValue = resp.cookies[0].value
        assertThat(sessionValue).isNotEmpty()

        val resp2 = MockHttpServletResponse()

        assertThat(
            userApi.register(
                RegisterRequest(
                    firstName = "first",
                    lastName = "last",
                    email = "test@example.com",
                    password = "very_secure_password"
                ), resp2
            ).success
        ).isFalse() // email already exists
        assertThat(resp2.cookies.size).isEqualTo(0)

        assertThat(
            userApi.login(
                LoginRequest(
                    email = "random@example.com",
                    password = "very_secure_password"
                ), resp2
            ).success
        ).isFalse() // email not exist
        assertThat(resp2.cookies.size).isEqualTo(0)

        assertThat(
            userApi.login(
                LoginRequest(
                    email = "test@example.com",
                    password = "very_secure_password_but_wrong"
                ), resp2
            ).success
        ).isFalse() // wrong password
        assertThat(resp2.cookies.size).isEqualTo(0)

        val result2 = userApi.login(
            LoginRequest(
                email = "test@example.com",
                password = "very_secure_password"
            ), resp2
        )
        assertThat(result2.success).isTrue()
        with(result2.userInfo!!) {
            assertThat(firstName).isEqualTo("first")
            assertThat(lastName).isEqualTo("last")
            assertThat(email).isEqualTo("test@example.com")
        }
        val newSessionId = resp2.cookies.single { it.name == "watcourses_session" }.value
        assertThat(newSessionId).isNotEmpty()
        assertThat(newSessionId).isNotEqualTo(sessionValue)
    }

    @Test
    fun `can logout`() {
        val resp = MockHttpServletResponse()
        assertThat(userApi.register(RegisterRequest(), resp).success).isEqualTo(false)
        assertThat(resp.cookies.size).isEqualTo(0)
        val result =
            userApi.register(
                RegisterRequest(
                    firstName = "first",
                    lastName = "last",
                    email = "test@example.com",
                    password = "very_secure_password"
                ), resp
            )
        assertThat(result.success).isEqualTo(true)
        with(result.userInfo!!) {
            assertThat(firstName).isEqualTo("first")
            assertThat(lastName).isEqualTo("last")
            assertThat(email).isEqualTo("test@example.com")
        }
        assertThat(resp.cookies[0].name).isEqualTo("watcourses_session")
        val sessionValue = resp.cookies[0].value
        assertThat(sessionValue).isNotEmpty

        val request = MockHttpServletRequest()
        request.setCookies(resp.cookies[0])
        assertThat(userApi.getUser(request).user!!.email).isEqualTo("test@example.com")
        assertThat(userApi.logout(request)).isTrue
        assertThat(userApi.getUser(request).user).isNull()
        assertThat(userApi.logout(request)).isFalse
    }
}