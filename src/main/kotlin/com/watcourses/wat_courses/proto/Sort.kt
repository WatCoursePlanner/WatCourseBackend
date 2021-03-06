// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: courses.proto
package com.watcourses.wat_courses.proto

import com.squareup.wire.EnumAdapter
import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.WireEnum
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
import kotlin.jvm.JvmStatic
import okio.ByteString

class Sort(
  @field:WireField(
    tag = 1,
    adapter = "com.watcourses.wat_courses.proto.Sort${'$'}SortBy#ADAPTER"
  )
  val sortBy: SortBy? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.watcourses.wat_courses.proto.Sort${'$'}Order#ADAPTER"
  )
  val order: Order? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<Sort, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is Sort) return false
    return unknownFields == other.unknownFields
        && sortBy == other.sortBy
        && order == other.order
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + sortBy.hashCode()
      result = result * 37 + order.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (sortBy != null) result += """sortBy=$sortBy"""
    if (order != null) result += """order=$order"""
    return result.joinToString(prefix = "Sort{", separator = ", ", postfix = "}")
  }

  fun copy(
    sortBy: SortBy? = this.sortBy,
    order: Order? = this.order,
    unknownFields: ByteString = this.unknownFields
  ): Sort = Sort(sortBy, order, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<Sort> = object : ProtoAdapter<Sort>(
      FieldEncoding.LENGTH_DELIMITED, 
      Sort::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.Sort"
    ) {
      override fun encodedSize(value: Sort): Int = 
        SortBy.ADAPTER.encodedSizeWithTag(1, value.sortBy) +
        Order.ADAPTER.encodedSizeWithTag(2, value.order) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: Sort) {
        SortBy.ADAPTER.encodeWithTag(writer, 1, value.sortBy)
        Order.ADAPTER.encodeWithTag(writer, 2, value.order)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): Sort {
        var sortBy: SortBy? = null
        var order: Order? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> try {
              sortBy = SortBy.ADAPTER.decode(reader)
            } catch (e: ProtoAdapter.EnumConstantNotFoundException) {
              reader.addUnknownField(tag, FieldEncoding.VARINT, e.value.toLong())
            }
            2 -> try {
              order = Order.ADAPTER.decode(reader)
            } catch (e: ProtoAdapter.EnumConstantNotFoundException) {
              reader.addUnknownField(tag, FieldEncoding.VARINT, e.value.toLong())
            }
            else -> reader.readUnknownField(tag)
          }
        }
        return Sort(
          sortBy = sortBy,
          order = order,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: Sort): Sort = value.copy(
        unknownFields = ByteString.EMPTY
      )
    }
  }

  enum class SortBy(
    override val value: Int
  ) : WireEnum {
    TITLE(1),

    CODE(2),

    LIKED(3),

    EASY(4),

    USEFUL(5),

    RATINGS_COUNT(6);

    companion object {
      @JvmField
      val ADAPTER: ProtoAdapter<SortBy> = object : EnumAdapter<SortBy>(
        SortBy::class
      ) {
        override fun fromValue(value: Int): SortBy? = SortBy.fromValue(value)
      }

      @JvmStatic
      fun fromValue(value: Int): SortBy? = when (value) {
        1 -> TITLE
        2 -> CODE
        3 -> LIKED
        4 -> EASY
        5 -> USEFUL
        6 -> RATINGS_COUNT
        else -> null
      }
    }
  }

  enum class Order(
    override val value: Int
  ) : WireEnum {
    ASC(1),

    DESC(2);

    companion object {
      @JvmField
      val ADAPTER: ProtoAdapter<Order> = object : EnumAdapter<Order>(
        Order::class
      ) {
        override fun fromValue(value: Int): Order? = Order.fromValue(value)
      }

      @JvmStatic
      fun fromValue(value: Int): Order? = when (value) {
        1 -> ASC
        2 -> DESC
        else -> null
      }
    }
  }
}
