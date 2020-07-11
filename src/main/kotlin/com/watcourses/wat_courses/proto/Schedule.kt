// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: courses.proto
package com.watcourses.wat_courses.proto

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.WireField
import com.squareup.wire.internal.redactElements
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

class Schedule(
  @field:WireField(
    tag = 1,
    adapter = "com.watcourses.wat_courses.proto.Schedule${'$'}TermSchedule#ADAPTER",
    label = WireField.Label.REPEATED
  )
  val terms: List<TermSchedule> = emptyList(),
  unknownFields: ByteString = ByteString.EMPTY
) : Message<Schedule, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is Schedule) return false
    return unknownFields == other.unknownFields
        && terms == other.terms
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + terms.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (terms.isNotEmpty()) result += """terms=$terms"""
    return result.joinToString(prefix = "Schedule{", separator = ", ", postfix = "}")
  }

  fun copy(terms: List<TermSchedule> = this.terms, unknownFields: ByteString = this.unknownFields):
      Schedule = Schedule(terms, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<Schedule> = object : ProtoAdapter<Schedule>(
      FieldEncoding.LENGTH_DELIMITED, 
      Schedule::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.Schedule"
    ) {
      override fun encodedSize(value: Schedule): Int = 
        TermSchedule.ADAPTER.asRepeated().encodedSizeWithTag(1, value.terms) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: Schedule) {
        TermSchedule.ADAPTER.asRepeated().encodeWithTag(writer, 1, value.terms)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): Schedule {
        val terms = mutableListOf<TermSchedule>()
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> terms.add(TermSchedule.ADAPTER.decode(reader))
            else -> reader.readUnknownField(tag)
          }
        }
        return Schedule(
          terms = terms,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: Schedule): Schedule = value.copy(
        terms = value.terms.redactElements(TermSchedule.ADAPTER),
        unknownFields = ByteString.EMPTY
      )
    }
  }

  class TermSchedule(
    @field:WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REPEATED
    )
    val courseCodes: List<String> = emptyList(),
    /**
     * e.g. 1A, 2B
     */
    @field:WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
    )
    val termName: String? = null,
    unknownFields: ByteString = ByteString.EMPTY
  ) : Message<TermSchedule, Nothing>(ADAPTER, unknownFields) {
    @Deprecated(
      message = "Shouldn't be used in Kotlin",
      level = DeprecationLevel.HIDDEN
    )
    override fun newBuilder(): Nothing = throw AssertionError()

    override fun equals(other: Any?): Boolean {
      if (other === this) return true
      if (other !is TermSchedule) return false
      return unknownFields == other.unknownFields
          && courseCodes == other.courseCodes
          && termName == other.termName
    }

    override fun hashCode(): Int {
      var result = super.hashCode
      if (result == 0) {
        result = unknownFields.hashCode()
        result = result * 37 + courseCodes.hashCode()
        result = result * 37 + termName.hashCode()
        super.hashCode = result
      }
      return result
    }

    override fun toString(): String {
      val result = mutableListOf<String>()
      if (courseCodes.isNotEmpty()) result += """courseCodes=${sanitize(courseCodes)}"""
      if (termName != null) result += """termName=${sanitize(termName)}"""
      return result.joinToString(prefix = "TermSchedule{", separator = ", ", postfix = "}")
    }

    fun copy(
      courseCodes: List<String> = this.courseCodes,
      termName: String? = this.termName,
      unknownFields: ByteString = this.unknownFields
    ): TermSchedule = TermSchedule(courseCodes, termName, unknownFields)

    companion object {
      @JvmField
      val ADAPTER: ProtoAdapter<TermSchedule> = object : ProtoAdapter<TermSchedule>(
        FieldEncoding.LENGTH_DELIMITED, 
        TermSchedule::class, 
        "type.googleapis.com/com.watcourses.wat_courses.proto.Schedule.TermSchedule"
      ) {
        override fun encodedSize(value: TermSchedule): Int = 
          ProtoAdapter.STRING.asRepeated().encodedSizeWithTag(1, value.courseCodes) +
          ProtoAdapter.STRING.encodedSizeWithTag(2, value.termName) +
          value.unknownFields.size

        override fun encode(writer: ProtoWriter, value: TermSchedule) {
          ProtoAdapter.STRING.asRepeated().encodeWithTag(writer, 1, value.courseCodes)
          ProtoAdapter.STRING.encodeWithTag(writer, 2, value.termName)
          writer.writeBytes(value.unknownFields)
        }

        override fun decode(reader: ProtoReader): TermSchedule {
          val courseCodes = mutableListOf<String>()
          var termName: String? = null
          val unknownFields = reader.forEachTag { tag ->
            when (tag) {
              1 -> courseCodes.add(ProtoAdapter.STRING.decode(reader))
              2 -> termName = ProtoAdapter.STRING.decode(reader)
              else -> reader.readUnknownField(tag)
            }
          }
          return TermSchedule(
            courseCodes = courseCodes,
            termName = termName,
            unknownFields = unknownFields
          )
        }

        override fun redact(value: TermSchedule): TermSchedule = value.copy(
          unknownFields = ByteString.EMPTY
        )
      }
    }
  }
}