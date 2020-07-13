package com.watcourses.wat_courses.utils

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.stereotype.Component
import kotlin.random.Random

private const val SALT_LEN = 16
private const val HASH_LEN = 32
private const val PARALLELISM = 1
private const val MEMORY = 4096
private const val ITERATIONS = 3

@Component
class PasswordEncoder {
    private val argon2 = Argon2PasswordEncoder(SALT_LEN, HASH_LEN, PARALLELISM, MEMORY, ITERATIONS)
    fun hash(password: String): String {
        return argon2.encode(password)
    }

    fun compare(hashedPassword: String, rawPassword: String): Boolean {
        return argon2.matches(rawPassword, hashedPassword)
    }
}