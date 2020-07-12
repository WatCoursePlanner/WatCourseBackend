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
import kotlin.hashCode
import kotlin.jvm.JvmField
import okio.ByteString

class PaginationInfoResponse(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val totalPages: Int? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val currentPage: Int? = null,
  @field:WireField(
    tag = 3,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val limit: Int? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<PaginationInfoResponse, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is PaginationInfoResponse) return false
    return unknownFields == other.unknownFields
        && totalPages == other.totalPages
        && currentPage == other.currentPage
        && limit == other.limit
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + totalPages.hashCode()
      result = result * 37 + currentPage.hashCode()
      result = result * 37 + limit.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (totalPages != null) result += """totalPages=$totalPages"""
    if (currentPage != null) result += """currentPage=$currentPage"""
    if (limit != null) result += """limit=$limit"""
    return result.joinToString(prefix = "PaginationInfoResponse{", separator = ", ", postfix = "}")
  }

  fun copy(
    totalPages: Int? = this.totalPages,
    currentPage: Int? = this.currentPage,
    limit: Int? = this.limit,
    unknownFields: ByteString = this.unknownFields
  ): PaginationInfoResponse = PaginationInfoResponse(totalPages, currentPage, limit, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<PaginationInfoResponse> = object :
        ProtoAdapter<PaginationInfoResponse>(
      FieldEncoding.LENGTH_DELIMITED, 
      PaginationInfoResponse::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.PaginationInfoResponse"
    ) {
      override fun encodedSize(value: PaginationInfoResponse): Int = 
        ProtoAdapter.INT32.encodedSizeWithTag(1, value.totalPages) +
        ProtoAdapter.INT32.encodedSizeWithTag(2, value.currentPage) +
        ProtoAdapter.INT32.encodedSizeWithTag(3, value.limit) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: PaginationInfoResponse) {
        ProtoAdapter.INT32.encodeWithTag(writer, 1, value.totalPages)
        ProtoAdapter.INT32.encodeWithTag(writer, 2, value.currentPage)
        ProtoAdapter.INT32.encodeWithTag(writer, 3, value.limit)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): PaginationInfoResponse {
        var totalPages: Int? = null
        var currentPage: Int? = null
        var limit: Int? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> totalPages = ProtoAdapter.INT32.decode(reader)
            2 -> currentPage = ProtoAdapter.INT32.decode(reader)
            3 -> limit = ProtoAdapter.INT32.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return PaginationInfoResponse(
          totalPages = totalPages,
          currentPage = currentPage,
          limit = limit,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: PaginationInfoResponse): PaginationInfoResponse = value.copy(
        unknownFields = ByteString.EMPTY
      )
    }
  }
}