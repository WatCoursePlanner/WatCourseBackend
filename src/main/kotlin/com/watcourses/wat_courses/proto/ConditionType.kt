// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: courses.proto
import com.squareup.wire.EnumAdapter
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.WireEnum
import kotlin.Int
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

enum class ConditionType(
  override val value: Int
) : WireEnum {
  TRUE(0),

  FALSE(1),

  AND(2),

  OR(3),

  NOT(4),

  HAS_COURSE(5),

  HAS_LABEL(6),

  SATISFIES_LIST(7);

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<ConditionType> = object : EnumAdapter<ConditionType>(
      ConditionType::class
    ) {
      override fun fromValue(value: Int): ConditionType? = ConditionType.fromValue(value)
    }

    @JvmStatic
    fun fromValue(value: Int): ConditionType? = when (value) {
      0 -> TRUE
      1 -> FALSE
      2 -> AND
      3 -> OR
      4 -> NOT
      5 -> HAS_COURSE
      6 -> HAS_LABEL
      7 -> SATISFIES_LIST
      else -> null
    }
  }
}
