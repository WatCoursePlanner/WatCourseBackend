// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: uwflow.proto
package com.watcourses.wat_courses.proto

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.WireField
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

class CoursesResponse(
  @field:WireField(
    tag = 1,
    adapter = "com.watcourses.wat_courses.proto.CoursesResponseData#ADAPTER"
  )
  val data: CoursesResponseData? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<CoursesResponse, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is CoursesResponse) return false
    return unknownFields == other.unknownFields
        && data == other.data
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + data.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (data != null) result += """data=$data"""
    return result.joinToString(prefix = "CoursesResponse{", separator = ", ", postfix = "}")
  }

  fun copy(data: CoursesResponseData? = this.data, unknownFields: ByteString = this.unknownFields):
      CoursesResponse = CoursesResponse(data, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<CoursesResponse> = object : ProtoAdapter<CoursesResponse>(
      FieldEncoding.LENGTH_DELIMITED, 
      CoursesResponse::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.CoursesResponse"
    ) {
      override fun encodedSize(value: CoursesResponse): Int = 
        CoursesResponseData.ADAPTER.encodedSizeWithTag(1, value.data) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: CoursesResponse) {
        CoursesResponseData.ADAPTER.encodeWithTag(writer, 1, value.data)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): CoursesResponse {
        var data: CoursesResponseData? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> data = CoursesResponseData.ADAPTER.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return CoursesResponse(
          data = data,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: CoursesResponse): CoursesResponse = value.copy(
        data = value.data?.let(CoursesResponseData.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }
  }
}
