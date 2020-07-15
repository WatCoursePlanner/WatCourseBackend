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

class ReParseRegressionTestResponse(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val total: Int? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val regressionNum: Int? = null,
  @field:WireField(
    tag = 3,
    adapter = "com.watcourses.wat_courses.proto.ReParseRegressionTestResponse${'$'}Result#ADAPTER",
    label = WireField.Label.REPEATED
  )
  val results: List<Result> = emptyList(),
  unknownFields: ByteString = ByteString.EMPTY
) : Message<ReParseRegressionTestResponse, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is ReParseRegressionTestResponse) return false
    return unknownFields == other.unknownFields
        && total == other.total
        && regressionNum == other.regressionNum
        && results == other.results
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + total.hashCode()
      result = result * 37 + regressionNum.hashCode()
      result = result * 37 + results.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (total != null) result += """total=$total"""
    if (regressionNum != null) result += """regressionNum=$regressionNum"""
    if (results.isNotEmpty()) result += """results=$results"""
    return result.joinToString(prefix = "ReParseRegressionTestResponse{", separator = ", ", postfix
        = "}")
  }

  fun copy(
    total: Int? = this.total,
    regressionNum: Int? = this.regressionNum,
    results: List<Result> = this.results,
    unknownFields: ByteString = this.unknownFields
  ): ReParseRegressionTestResponse = ReParseRegressionTestResponse(total, regressionNum, results,
      unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<ReParseRegressionTestResponse> = object :
        ProtoAdapter<ReParseRegressionTestResponse>(
      FieldEncoding.LENGTH_DELIMITED, 
      ReParseRegressionTestResponse::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.ReParseRegressionTestResponse"
    ) {
      override fun encodedSize(value: ReParseRegressionTestResponse): Int = 
        ProtoAdapter.INT32.encodedSizeWithTag(1, value.total) +
        ProtoAdapter.INT32.encodedSizeWithTag(2, value.regressionNum) +
        Result.ADAPTER.asRepeated().encodedSizeWithTag(3, value.results) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: ReParseRegressionTestResponse) {
        ProtoAdapter.INT32.encodeWithTag(writer, 1, value.total)
        ProtoAdapter.INT32.encodeWithTag(writer, 2, value.regressionNum)
        Result.ADAPTER.asRepeated().encodeWithTag(writer, 3, value.results)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): ReParseRegressionTestResponse {
        var total: Int? = null
        var regressionNum: Int? = null
        val results = mutableListOf<Result>()
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> total = ProtoAdapter.INT32.decode(reader)
            2 -> regressionNum = ProtoAdapter.INT32.decode(reader)
            3 -> results.add(Result.ADAPTER.decode(reader))
            else -> reader.readUnknownField(tag)
          }
        }
        return ReParseRegressionTestResponse(
          total = total,
          regressionNum = regressionNum,
          results = results,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: ReParseRegressionTestResponse): ReParseRegressionTestResponse =
          value.copy(
        results = value.results.redactElements(Result.ADAPTER),
        unknownFields = ByteString.EMPTY
      )
    }
  }

  class Result(
    @field:WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
    )
    val rawRule: String? = null,
    @field:WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
    )
    val old: String? = null,
    @field:WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
    )
    val new: String? = null,
    @field:WireField(
      tag = 4,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
    )
    val error: String? = null,
    unknownFields: ByteString = ByteString.EMPTY
  ) : Message<Result, Nothing>(ADAPTER, unknownFields) {
    @Deprecated(
      message = "Shouldn't be used in Kotlin",
      level = DeprecationLevel.HIDDEN
    )
    override fun newBuilder(): Nothing = throw AssertionError()

    override fun equals(other: Any?): Boolean {
      if (other === this) return true
      if (other !is Result) return false
      return unknownFields == other.unknownFields
          && rawRule == other.rawRule
          && old == other.old
          && new == other.new
          && error == other.error
    }

    override fun hashCode(): Int {
      var result = super.hashCode
      if (result == 0) {
        result = unknownFields.hashCode()
        result = result * 37 + rawRule.hashCode()
        result = result * 37 + old.hashCode()
        result = result * 37 + new.hashCode()
        result = result * 37 + error.hashCode()
        super.hashCode = result
      }
      return result
    }

    override fun toString(): String {
      val result = mutableListOf<String>()
      if (rawRule != null) result += """rawRule=${sanitize(rawRule)}"""
      if (old != null) result += """old=${sanitize(old)}"""
      if (new != null) result += """new=${sanitize(new)}"""
      if (error != null) result += """error=${sanitize(error)}"""
      return result.joinToString(prefix = "Result{", separator = ", ", postfix = "}")
    }

    fun copy(
      rawRule: String? = this.rawRule,
      old: String? = this.old,
      new: String? = this.new,
      error: String? = this.error,
      unknownFields: ByteString = this.unknownFields
    ): Result = Result(rawRule, old, new, error, unknownFields)

    companion object {
      @JvmField
      val ADAPTER: ProtoAdapter<Result> = object : ProtoAdapter<Result>(
        FieldEncoding.LENGTH_DELIMITED, 
        Result::class, 
        "type.googleapis.com/com.watcourses.wat_courses.proto.ReParseRegressionTestResponse.Result"
      ) {
        override fun encodedSize(value: Result): Int = 
          ProtoAdapter.STRING.encodedSizeWithTag(1, value.rawRule) +
          ProtoAdapter.STRING.encodedSizeWithTag(2, value.old) +
          ProtoAdapter.STRING.encodedSizeWithTag(3, value.new) +
          ProtoAdapter.STRING.encodedSizeWithTag(4, value.error) +
          value.unknownFields.size

        override fun encode(writer: ProtoWriter, value: Result) {
          ProtoAdapter.STRING.encodeWithTag(writer, 1, value.rawRule)
          ProtoAdapter.STRING.encodeWithTag(writer, 2, value.old)
          ProtoAdapter.STRING.encodeWithTag(writer, 3, value.new)
          ProtoAdapter.STRING.encodeWithTag(writer, 4, value.error)
          writer.writeBytes(value.unknownFields)
        }

        override fun decode(reader: ProtoReader): Result {
          var rawRule: String? = null
          var old: String? = null
          var new: String? = null
          var error: String? = null
          val unknownFields = reader.forEachTag { tag ->
            when (tag) {
              1 -> rawRule = ProtoAdapter.STRING.decode(reader)
              2 -> old = ProtoAdapter.STRING.decode(reader)
              3 -> new = ProtoAdapter.STRING.decode(reader)
              4 -> error = ProtoAdapter.STRING.decode(reader)
              else -> reader.readUnknownField(tag)
            }
          }
          return Result(
            rawRule = rawRule,
            old = old,
            new = new,
            error = error,
            unknownFields = unknownFields
          )
        }

        override fun redact(value: Result): Result = value.copy(
          unknownFields = ByteString.EMPTY
        )
      }
    }
  }
}