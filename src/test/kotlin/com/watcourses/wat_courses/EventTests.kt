package com.watcourses.wat_courses

import com.watcourses.wat_courses.api.EventApi
import com.watcourses.wat_courses.api.UserApi
import com.watcourses.wat_courses.persistence.DbEventRepo
import com.watcourses.wat_courses.persistence.DbUserRepo
import com.watcourses.wat_courses.proto.EventRequest
import com.watcourses.wat_courses.proto.RegisterRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc


@SpringBootTest
@ActiveProfiles("test")
class EventTests {
    @Autowired
    private lateinit var userApi: UserApi

    @Autowired
    private lateinit var eventApi: EventApi

    @Autowired
    private lateinit var dbEventRepo: DbEventRepo

    @Autowired
    private lateinit var dbUserRepo: DbUserRepo

    @Autowired
    private lateinit var mockMvc: MockMvc

    private fun requestFromResponse(resp: MockHttpServletResponse): MockHttpServletRequest {
        val req = MockHttpServletRequest()
        req.setCookies(*resp.cookies)
        return req
    }

    @Test
    fun `logged in user has user id as identifier`() {
        val resp = MockHttpServletResponse()
        userApi.register(
            RegisterRequest(firstName = "first", lastName = "last", email = "1@a.com", password = "1"), resp
        )
        val identifier = dbUserRepo.findAll().single()!!.id.toString()

        eventApi.event(EventRequest(type = "type", subject = "ECE 123", data = "123"), requestFromResponse(resp))
        eventApi.event(EventRequest(type = "type2", subject = "ECE 124", data = "124"), requestFromResponse(resp))

        val events = dbEventRepo.findAll()
        assertThat(events).hasSize(2)
        assertThat(events.first()!!.type).isEqualTo("type")
        assertThat(events.first()!!.subject).isEqualTo("ECE 123")
        assertThat(events.first()!!.data).isEqualTo("123")
        assertThat(events.first()!!.identifier).isEqualTo(identifier)
        assertThat(events.last()!!.identifier).isEqualTo(identifier)
    }

    @Test
    fun `guest user has same identifier`() {
        //TODO:add test
    }
}