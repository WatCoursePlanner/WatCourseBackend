// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: user.proto
package com.watcourses.wat_courses.proto

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.WireField
import com.squareup.wire.internal.sanitize
import kotlin.Any
import kotlin.AssertionError
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Int
import kotlin.Nothing
import kotlin.String
import kotlin.hashCode
import kotlin.jvm.JvmField
import okio.ByteString

/**
 * For a new user who uses email+password login: RegisterRequest -> LoginOrRegisterResponse
 */
class RegisterRequest(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val firstName: String? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val lastName: String? = null,
  @field:WireField(
    tag = 3,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val email: String? = null,
  @field:WireField(
    tag = 4,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val password: String? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<RegisterRequest, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is RegisterRequest) return false
    return unknownFields == other.unknownFields
        && firstName == other.firstName
        && lastName == other.lastName
        && email == other.email
        && password == other.password
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + firstName.hashCode()
      result = result * 37 + lastName.hashCode()
      result = result * 37 + email.hashCode()
      result = result * 37 + password.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (firstName != null) result += """firstName=${sanitize(firstName)}"""
    if (lastName != null) result += """lastName=${sanitize(lastName)}"""
    if (email != null) result += """email=${sanitize(email)}"""
    if (password != null) result += """password=${sanitize(password)}"""
    return result.joinToString(prefix = "RegisterRequest{", separator = ", ", postfix = "}")
  }

  fun copy(
    firstName: String? = this.firstName,
    lastName: String? = this.lastName,
    email: String? = this.email,
    password: String? = this.password,
    unknownFields: ByteString = this.unknownFields
  ): RegisterRequest = RegisterRequest(firstName, lastName, email, password, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<RegisterRequest> = object : ProtoAdapter<RegisterRequest>(
      FieldEncoding.LENGTH_DELIMITED, 
      RegisterRequest::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.RegisterRequest"
    ) {
      override fun encodedSize(value: RegisterRequest): Int = 
        ProtoAdapter.STRING.encodedSizeWithTag(1, value.firstName) +
        ProtoAdapter.STRING.encodedSizeWithTag(2, value.lastName) +
        ProtoAdapter.STRING.encodedSizeWithTag(3, value.email) +
        ProtoAdapter.STRING.encodedSizeWithTag(4, value.password) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: RegisterRequest) {
        ProtoAdapter.STRING.encodeWithTag(writer, 1, value.firstName)
        ProtoAdapter.STRING.encodeWithTag(writer, 2, value.lastName)
        ProtoAdapter.STRING.encodeWithTag(writer, 3, value.email)
        ProtoAdapter.STRING.encodeWithTag(writer, 4, value.password)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): RegisterRequest {
        var firstName: String? = null
        var lastName: String? = null
        var email: String? = null
        var password: String? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> firstName = ProtoAdapter.STRING.decode(reader)
            2 -> lastName = ProtoAdapter.STRING.decode(reader)
            3 -> email = ProtoAdapter.STRING.decode(reader)
            4 -> password = ProtoAdapter.STRING.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return RegisterRequest(
          firstName = firstName,
          lastName = lastName,
          email = email,
          password = password,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: RegisterRequest): RegisterRequest = value.copy(
        unknownFields = ByteString.EMPTY
      )
    }
  }
}