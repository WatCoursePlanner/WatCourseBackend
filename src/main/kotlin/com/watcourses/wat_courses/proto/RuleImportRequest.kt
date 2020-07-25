// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: admin.proto
package com.watcourses.wat_courses.proto

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.WireField
import com.squareup.wire.internal.redactElements
import kotlin.Any
import kotlin.AssertionError
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Int
import kotlin.Nothing
import kotlin.String
import kotlin.collections.List
import kotlin.jvm.JvmField
import okio.ByteString

class RuleImportRequest(
  @field:WireField(
    tag = 1,
    adapter = "com.watcourses.wat_courses.proto.RuleImportItem#ADAPTER",
    label = WireField.Label.REPEATED
  )
  val items: List<RuleImportItem> = emptyList(),
  unknownFields: ByteString = ByteString.EMPTY
) : Message<RuleImportRequest, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is RuleImportRequest) return false
    return unknownFields == other.unknownFields
        && items == other.items
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + items.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (items.isNotEmpty()) result += """items=$items"""
    return result.joinToString(prefix = "RuleImportRequest{", separator = ", ", postfix = "}")
  }

  fun copy(items: List<RuleImportItem> = this.items, unknownFields: ByteString =
      this.unknownFields): RuleImportRequest = RuleImportRequest(items, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<RuleImportRequest> = object : ProtoAdapter<RuleImportRequest>(
      FieldEncoding.LENGTH_DELIMITED, 
      RuleImportRequest::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.RuleImportRequest"
    ) {
      override fun encodedSize(value: RuleImportRequest): Int = 
        RuleImportItem.ADAPTER.asRepeated().encodedSizeWithTag(1, value.items) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: RuleImportRequest) {
        RuleImportItem.ADAPTER.asRepeated().encodeWithTag(writer, 1, value.items)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): RuleImportRequest {
        val items = mutableListOf<RuleImportItem>()
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> items.add(RuleImportItem.ADAPTER.decode(reader))
            else -> reader.readUnknownField(tag)
          }
        }
        return RuleImportRequest(
          items = items,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: RuleImportRequest): RuleImportRequest = value.copy(
        items = value.items.redactElements(RuleImportItem.ADAPTER),
        unknownFields = ByteString.EMPTY
      )
    }
  }
}
