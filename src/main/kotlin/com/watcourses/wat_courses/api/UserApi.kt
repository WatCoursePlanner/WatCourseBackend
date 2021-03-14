package com.watcourses.wat_courses.api

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.watcourses.wat_courses.AppProperties
import com.watcourses.wat_courses.persistence.DbUser
import com.watcourses.wat_courses.persistence.DbUserRepo
import com.watcourses.wat_courses.proto.*
import com.watcourses.wat_courses.utils.PasswordEncoder
import com.watcourses.wat_courses.utils.SessionManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class UserApi(
    val dbUserRepo: DbUserRepo,
    val passwordEncoder: PasswordEncoder,
    val sessionManager: SessionManager,
    val appProperties: AppProperties
) {
    private val transport = NetHttpTransport()
    private val jsonFactory = JacksonFactory()

    @PostMapping("/user/login")
    fun login(@RequestBody loginRequest: LoginRequest, response: HttpServletResponse): LoginOrRegisterResponse {
        val email = loginRequest.email?.takeIf { it.isNotEmpty() }
            ?: return LoginOrRegisterResponse(success = false, reason = "Email not provided")

        val password = loginRequest.password?.takeIf { it.isNotEmpty() }
            ?: return LoginOrRegisterResponse(success = false, reason = "Password not provided")

        val dbUser = dbUserRepo.findByEmail(email)

        if (dbUser?.password != null && passwordEncoder.compare(dbUser.password!!, password)) {
            sessionManager.generateAndSendSessionId(response, dbUser)
            dbUserRepo.save(dbUser)
            return LoginOrRegisterResponse(success = true, userInfo = dbUser.toProto())
        }

        return LoginOrRegisterResponse(
            success = false, reason = "Username or password incorrect"
        )
    }

    @PostMapping("/user/register")
    fun register(
        @RequestBody registerRequest: RegisterRequest,
        response: HttpServletResponse
    ): LoginOrRegisterResponse {
        val email = registerRequest.email?.takeIf { it.isNotEmpty() }
            ?: return LoginOrRegisterResponse(success = false, reason = "Email not provided")

        val password = registerRequest.password?.takeIf { it.isNotEmpty() }
            ?: return LoginOrRegisterResponse(success = false, reason = "Password not provided")

        val firstName = registerRequest.firstName?.takeIf { it.isNotEmpty() }
            ?: return LoginOrRegisterResponse(success = false, reason = "First name not provided")

        val lastName = registerRequest.lastName?.takeIf { it.isNotEmpty() }
            ?: return LoginOrRegisterResponse(success = false, reason = "Last Name not provided")

        if (dbUserRepo.findByEmail(email) != null) {
            return LoginOrRegisterResponse(success = false, reason = "Email address already exists")
        }

        val dbUser = DbUser(
            email = email,
            firstName = firstName,
            lastName = lastName,
            password = passwordEncoder.hash(password),
            sessionId = "",
        )

        sessionManager.generateAndSendSessionId(response, dbUser)
        dbUserRepo.save(dbUser)

        return LoginOrRegisterResponse(success = true, userInfo = dbUser.toProto())
    }

    @PostMapping("/user/google")
    fun googleLoginOrRegister(
        @RequestBody googleLoginOrRegisterRequest: GoogleLoginOrRegisterRequest,
        response: HttpServletResponse
    ): LoginOrRegisterResponse {
        val token = googleLoginOrRegisterRequest.token?.takeIf { it.isNotEmpty() }
            ?: return LoginOrRegisterResponse(success = false, reason = "Token not provided")

        val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(Collections.singletonList(appProperties.google_client_id))
            .build()

        val idToken = verifier.verify(token)
            ?: return LoginOrRegisterResponse(success = false, reason = "Illegal user token")

        val payload = idToken.payload
        val userId = payload.subject

        var dbUser = dbUserRepo.findByGoogleId(userId)
        if (dbUser == null) {
            // Register a new user
            val email = payload.email

            if (dbUserRepo.findByEmail(email) != null) {
                return LoginOrRegisterResponse(success = false, reason = "Email address already exists")
            }

            dbUser = DbUser(
                email = email,
                firstName = payload["given_name"] as String,
                lastName = payload["family_name"] as String,
                password = null,
                sessionId = "",
                googleId = userId,
                pictureUrl = payload["picture"] as String?
            )
        }
        sessionManager.generateAndSendSessionId(response, dbUser)
        dbUserRepo.save(dbUser)

        return LoginOrRegisterResponse(success = true, userInfo = dbUser.toProto())
    }

    @PutMapping("/user/data")
    fun putData(
        @RequestBody request: SetUserDataRequest, httpRequest: HttpServletRequest
    ): Boolean {
        val user = sessionManager.getCurrentUser(httpRequest) ?: return false
        user.data = request.data
        dbUserRepo.save(user)
        return true
    }

    // Use POST instead of GET to prevent json hijacking
    @PostMapping("/user/data")
    fun getData(httpRequest: HttpServletRequest): String {
        return sessionManager.getCurrentUser(httpRequest)?.data ?: ""
    }

    @PostMapping("/user/get")
    fun getUser(httpRequest: HttpServletRequest): GetUserResponse {
        return GetUserResponse(user = sessionManager.getCurrentUser(httpRequest)?.toProto())
    }
}