// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: admin.proto
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
import kotlin.collections.Map
import kotlin.jvm.JvmField
import okio.ByteString

class RuleImprotResponse(
  @field:WireField(
    tag = 1,
    keyAdapter = "com.squareup.wire.ProtoAdapter#STRING",
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val result: Map<String, String> = emptyMap(),
  unknownFields: ByteString = ByteString.EMPTY
) : Message<RuleImprotResponse, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is RuleImprotResponse) return false
    return unknownFields == other.unknownFields
        && result == other.result
  }

  override fun hashCode(): Int {
    var result_ = super.hashCode
    if (result_ == 0) {
      result_ = unknownFields.hashCode()
      result_ = result_ * 37 + result.hashCode()
      super.hashCode = result_
    }
    return result_
  }

  override fun toString(): String {
    val result_ = mutableListOf<String>()
    if (result.isNotEmpty()) result_ += """result=$result"""
    return result_.joinToString(prefix = "RuleImprotResponse{", separator = ", ", postfix = "}")
  }

  fun copy(result: Map<String, String> = this.result, unknownFields: ByteString =
      this.unknownFields): RuleImprotResponse = RuleImprotResponse(result, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<RuleImprotResponse> = object : ProtoAdapter<RuleImprotResponse>(
      FieldEncoding.LENGTH_DELIMITED, 
      RuleImprotResponse::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.RuleImprotResponse"
    ) {
      private val resultAdapter: ProtoAdapter<Map<String, String>> =
          ProtoAdapter.newMapAdapter(ProtoAdapter.STRING, ProtoAdapter.STRING)

      override fun encodedSize(value: RuleImprotResponse): Int = 
        resultAdapter.encodedSizeWithTag(1, value.result) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: RuleImprotResponse) {
        resultAdapter.encodeWithTag(writer, 1, value.result)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): RuleImprotResponse {
        val result = mutableMapOf<String, String>()
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> result.putAll(resultAdapter.decode(reader))
            else -> reader.readUnknownField(tag)
          }
        }
        return RuleImprotResponse(
          result = result,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: RuleImprotResponse): RuleImprotResponse = value.copy(
        unknownFields = ByteString.EMPTY
      )
    }
  }
}
