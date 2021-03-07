package com.watcourses.wat_courses.persistence

import com.watcourses.wat_courses.proto.UserInfo
import javax.persistence.*

@Entity(name = "user")
@Table(
    name = "users", indexes = [
        Index(name = "idx_email", columnList = "email", unique = true),
        Index(name = "idx_session", columnList = "sessionId", unique = true),
        Index(name = "idx_google_id", columnList = "googleId", unique = true)
    ]
)
data class DbUser(
    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column
    var password: String?,

    @Column(nullable = false)
    var sessionId: String,

    @Column
    var googleId: String? = null,

    @Column
    var pictureUrl: String? = null,

    @Column
    var data: String? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var studentProfile: DbStudentProfile? = null,

    @Id @GeneratedValue
    var id: Long? = null
) {
    fun toProto(): UserInfo {
        return UserInfo(
            firstName = firstName,
            lastName = lastName,
            email = email,
            pictureUrl = pictureUrl
        )
    }
}
