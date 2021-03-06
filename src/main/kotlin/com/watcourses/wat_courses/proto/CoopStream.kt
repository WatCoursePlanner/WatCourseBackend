// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: courses.proto
package com.watcourses.wat_courses.proto

import com.squareup.wire.EnumAdapter
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.WireEnum
import kotlin.Int
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

enum class CoopStream(
  override val value: Int
) : WireEnum {
  NO_COOP(0),

  STREAM_4(1),

  STREAM_8(2);

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<CoopStream> = object : EnumAdapter<CoopStream>(
      CoopStream::class
    ) {
      override fun fromValue(value: Int): CoopStream? = CoopStream.fromValue(value)
    }

    @JvmStatic
    fun fromValue(value: Int): CoopStream? = when (value) {
      0 -> NO_COOP
      1 -> STREAM_4
      2 -> STREAM_8
      else -> null
    }
  }
}
