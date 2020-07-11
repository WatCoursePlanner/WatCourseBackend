// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: courses.proto
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
import kotlin.hashCode
import kotlin.jvm.JvmField
import okio.ByteString

class ReParseConditionsResponse(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val total: Int? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val success: Int? = null,
  @field:WireField(
    tag = 3,
    keyAdapter = "com.squareup.wire.ProtoAdapter#STRING",
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val succeedResults: Map<String, String> = emptyMap(),
  @field:WireField(
    tag = 4,
    keyAdapter = "com.squareup.wire.ProtoAdapter#STRING",
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val failedResults: Map<String, String> = emptyMap(),
  @field:WireField(
    tag = 5,
    adapter = "com.squareup.wire.ProtoAdapter#BOOL"
  )
  val dryRun: Boolean? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<ReParseConditionsResponse, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is ReParseConditionsResponse) return false
    return unknownFields == other.unknownFields
        && total == other.total
        && success == other.success
        && succeedResults == other.succeedResults
        && failedResults == other.failedResults
        && dryRun == other.dryRun
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + total.hashCode()
      result = result * 37 + success.hashCode()
      result = result * 37 + succeedResults.hashCode()
      result = result * 37 + failedResults.hashCode()
      result = result * 37 + dryRun.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (total != null) result += """total=$total"""
    if (success != null) result += """success=$success"""
    if (succeedResults.isNotEmpty()) result += """succeedResults=$succeedResults"""
    if (failedResults.isNotEmpty()) result += """failedResults=$failedResults"""
    if (dryRun != null) result += """dryRun=$dryRun"""
    return result.joinToString(prefix = "ReParseConditionsResponse{", separator = ", ", postfix =
        "}")
  }

  fun copy(
    total: Int? = this.total,
    success: Int? = this.success,
    succeedResults: Map<String, String> = this.succeedResults,
    failedResults: Map<String, String> = this.failedResults,
    dryRun: Boolean? = this.dryRun,
    unknownFields: ByteString = this.unknownFields
  ): ReParseConditionsResponse = ReParseConditionsResponse(total, success, succeedResults,
      failedResults, dryRun, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<ReParseConditionsResponse> = object :
        ProtoAdapter<ReParseConditionsResponse>(
      FieldEncoding.LENGTH_DELIMITED, 
      ReParseConditionsResponse::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.ReParseConditionsResponse"
    ) {
      private val succeedResultsAdapter: ProtoAdapter<Map<String, String>> =
          ProtoAdapter.newMapAdapter(ProtoAdapter.STRING, ProtoAdapter.STRING)

      private val failedResultsAdapter: ProtoAdapter<Map<String, String>> =
          ProtoAdapter.newMapAdapter(ProtoAdapter.STRING, ProtoAdapter.STRING)

      override fun encodedSize(value: ReParseConditionsResponse): Int = 
        ProtoAdapter.INT32.encodedSizeWithTag(1, value.total) +
        ProtoAdapter.INT32.encodedSizeWithTag(2, value.success) +
        succeedResultsAdapter.encodedSizeWithTag(3, value.succeedResults) +
        failedResultsAdapter.encodedSizeWithTag(4, value.failedResults) +
        ProtoAdapter.BOOL.encodedSizeWithTag(5, value.dryRun) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: ReParseConditionsResponse) {
        ProtoAdapter.INT32.encodeWithTag(writer, 1, value.total)
        ProtoAdapter.INT32.encodeWithTag(writer, 2, value.success)
        succeedResultsAdapter.encodeWithTag(writer, 3, value.succeedResults)
        failedResultsAdapter.encodeWithTag(writer, 4, value.failedResults)
        ProtoAdapter.BOOL.encodeWithTag(writer, 5, value.dryRun)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): ReParseConditionsResponse {
        var total: Int? = null
        var success: Int? = null
        val succeedResults = mutableMapOf<String, String>()
        val failedResults = mutableMapOf<String, String>()
        var dryRun: Boolean? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> total = ProtoAdapter.INT32.decode(reader)
            2 -> success = ProtoAdapter.INT32.decode(reader)
            3 -> succeedResults.putAll(succeedResultsAdapter.decode(reader))
            4 -> failedResults.putAll(failedResultsAdapter.decode(reader))
            5 -> dryRun = ProtoAdapter.BOOL.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return ReParseConditionsResponse(
          total = total,
          success = success,
          succeedResults = succeedResults,
          failedResults = failedResults,
          dryRun = dryRun,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: ReParseConditionsResponse): ReParseConditionsResponse = value.copy(
        unknownFields = ByteString.EMPTY
      )
    }
  }
}
