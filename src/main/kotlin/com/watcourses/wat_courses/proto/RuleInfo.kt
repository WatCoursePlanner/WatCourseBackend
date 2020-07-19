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

class RuleInfo(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val rawString: String? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val logicString: String? = null,
  @field:WireField(
    tag = 3,
    adapter = "com.squareup.wire.ProtoAdapter#BOOL"
  )
  val fullyResolved: Boolean? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<RuleInfo, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is RuleInfo) return false
    return unknownFields == other.unknownFields
        && rawString == other.rawString
        && logicString == other.logicString
        && fullyResolved == other.fullyResolved
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + rawString.hashCode()
      result = result * 37 + logicString.hashCode()
      result = result * 37 + fullyResolved.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (rawString != null) result += """rawString=${sanitize(rawString)}"""
    if (logicString != null) result += """logicString=${sanitize(logicString)}"""
    if (fullyResolved != null) result += """fullyResolved=$fullyResolved"""
    return result.joinToString(prefix = "RuleInfo{", separator = ", ", postfix = "}")
  }

  fun copy(
    rawString: String? = this.rawString,
    logicString: String? = this.logicString,
    fullyResolved: Boolean? = this.fullyResolved,
    unknownFields: ByteString = this.unknownFields
  ): RuleInfo = RuleInfo(rawString, logicString, fullyResolved, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<RuleInfo> = object : ProtoAdapter<RuleInfo>(
      FieldEncoding.LENGTH_DELIMITED, 
      RuleInfo::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.RuleInfo"
    ) {
      override fun encodedSize(value: RuleInfo): Int = 
        ProtoAdapter.STRING.encodedSizeWithTag(1, value.rawString) +
        ProtoAdapter.STRING.encodedSizeWithTag(2, value.logicString) +
        ProtoAdapter.BOOL.encodedSizeWithTag(3, value.fullyResolved) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: RuleInfo) {
        ProtoAdapter.STRING.encodeWithTag(writer, 1, value.rawString)
        ProtoAdapter.STRING.encodeWithTag(writer, 2, value.logicString)
        ProtoAdapter.BOOL.encodeWithTag(writer, 3, value.fullyResolved)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): RuleInfo {
        var rawString: String? = null
        var logicString: String? = null
        var fullyResolved: Boolean? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> rawString = ProtoAdapter.STRING.decode(reader)
            2 -> logicString = ProtoAdapter.STRING.decode(reader)
            3 -> fullyResolved = ProtoAdapter.BOOL.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return RuleInfo(
          rawString = rawString,
          logicString = logicString,
          fullyResolved = fullyResolved,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: RuleInfo): RuleInfo = value.copy(
        unknownFields = ByteString.EMPTY
      )
    }
  }
}
