// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: courses.proto
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

class HelloReply(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val message: String? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<HelloReply, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is HelloReply) return false
    return unknownFields == other.unknownFields
        && message == other.message
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + message.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (message != null) result += """message=${sanitize(message)}"""
    return result.joinToString(prefix = "HelloReply{", separator = ", ", postfix = "}")
  }

  fun copy(message: String? = this.message, unknownFields: ByteString = this.unknownFields):
      HelloReply = HelloReply(message, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<HelloReply> = object : ProtoAdapter<HelloReply>(
      FieldEncoding.LENGTH_DELIMITED, 
      HelloReply::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.HelloReply"
    ) {
      override fun encodedSize(value: HelloReply): Int = 
        ProtoAdapter.STRING.encodedSizeWithTag(1, value.message) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: HelloReply) {
        ProtoAdapter.STRING.encodeWithTag(writer, 1, value.message)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): HelloReply {
        var message: String? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> message = ProtoAdapter.STRING.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return HelloReply(
          message = message,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: HelloReply): HelloReply = value.copy(
        unknownFields = ByteString.EMPTY
      )
    }
  }
}
