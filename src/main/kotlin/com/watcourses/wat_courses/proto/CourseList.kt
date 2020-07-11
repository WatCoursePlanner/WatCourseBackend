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
import kotlin.collections.List
import kotlin.hashCode
import kotlin.jvm.JvmField
import okio.ByteString

class CourseList(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val name: String? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.REPEATED
  )
  val courses: List<String> = emptyList(),
  unknownFields: ByteString = ByteString.EMPTY
) : Message<CourseList, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is CourseList) return false
    return unknownFields == other.unknownFields
        && name == other.name
        && courses == other.courses
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + name.hashCode()
      result = result * 37 + courses.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (name != null) result += """name=${sanitize(name)}"""
    if (courses.isNotEmpty()) result += """courses=${sanitize(courses)}"""
    return result.joinToString(prefix = "CourseList{", separator = ", ", postfix = "}")
  }

  fun copy(
    name: String? = this.name,
    courses: List<String> = this.courses,
    unknownFields: ByteString = this.unknownFields
  ): CourseList = CourseList(name, courses, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<CourseList> = object : ProtoAdapter<CourseList>(
      FieldEncoding.LENGTH_DELIMITED, 
      CourseList::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.CourseList"
    ) {
      override fun encodedSize(value: CourseList): Int = 
        ProtoAdapter.STRING.encodedSizeWithTag(1, value.name) +
        ProtoAdapter.STRING.asRepeated().encodedSizeWithTag(2, value.courses) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: CourseList) {
        ProtoAdapter.STRING.encodeWithTag(writer, 1, value.name)
        ProtoAdapter.STRING.asRepeated().encodeWithTag(writer, 2, value.courses)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): CourseList {
        var name: String? = null
        val courses = mutableListOf<String>()
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> name = ProtoAdapter.STRING.decode(reader)
            2 -> courses.add(ProtoAdapter.STRING.decode(reader))
            else -> reader.readUnknownField(tag)
          }
        }
        return CourseList(
          name = name,
          courses = courses,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: CourseList): CourseList = value.copy(
        unknownFields = ByteString.EMPTY
      )
    }
  }
}