package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.persistence.DbEvent
import com.watcourses.wat_courses.persistence.DbEventRepo
import com.watcourses.wat_courses.proto.EventRequest
import com.watcourses.wat_courses.utils.SessionManager
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class EventApi(
    private val dbEventRepo: DbEventRepo,
    private val sessionManager: SessionManager
) {
    @PostMapping("/event")
    fun event(@RequestBody request: EventRequest, httpRequest: HttpServletRequest) {
        val identifier = sessionManager.getCurrentUser(httpRequest)?.id?.toString()
            ?: httpRequest.session.id
            ?: sessionManager.generateGuestSessionToken()

        dbEventRepo.save(
            DbEvent(
                type = request.type!!,
                subject = request.subject!!,
                data = request.data,
                identifier = identifier
            )
        )
    }
}
