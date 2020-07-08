// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: courses.proto
import com.squareup.wire.EnumAdapter
import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.WireEnum
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
import kotlin.jvm.JvmStatic
import okio.ByteString

class Condition(
  @field:WireField(
    tag = 1,
    adapter = "Condition${'$'}Type#ADAPTER"
  )
  val type: Type? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val data: String? = null,
  @field:WireField(
    tag = 3,
    adapter = "Condition#ADAPTER",
    label = WireField.Label.REPEATED
  )
  val operands: List<Condition> = emptyList(),
  unknownFields: ByteString = ByteString.EMPTY
) : Message<Condition, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is Condition) return false
    return unknownFields == other.unknownFields
        && type == other.type
        && data == other.data
        && operands == other.operands
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + type.hashCode()
      result = result * 37 + data.hashCode()
      result = result * 37 + operands.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (type != null) result += """type=$type"""
    if (data != null) result += """data=${sanitize(data)}"""
    if (operands.isNotEmpty()) result += """operands=$operands"""
    return result.joinToString(prefix = "Condition{", separator = ", ", postfix = "}")
  }

  fun copy(
    type: Type? = this.type,
    data: String? = this.data,
    operands: List<Condition> = this.operands,
    unknownFields: ByteString = this.unknownFields
  ): Condition = Condition(type, data, operands, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<Condition> = object : ProtoAdapter<Condition>(
      FieldEncoding.LENGTH_DELIMITED, 
      Condition::class, 
      "type.googleapis.com/Condition"
    ) {
      override fun encodedSize(value: Condition): Int = 
        Type.ADAPTER.encodedSizeWithTag(1, value.type) +
        ProtoAdapter.STRING.encodedSizeWithTag(2, value.data) +
        Condition.ADAPTER.asRepeated().encodedSizeWithTag(3, value.operands) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: Condition) {
        Type.ADAPTER.encodeWithTag(writer, 1, value.type)
        ProtoAdapter.STRING.encodeWithTag(writer, 2, value.data)
        Condition.ADAPTER.asRepeated().encodeWithTag(writer, 3, value.operands)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): Condition {
        var type: Type? = null
        var data: String? = null
        val operands = mutableListOf<Condition>()
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> try {
              type = Type.ADAPTER.decode(reader)
            } catch (e: ProtoAdapter.EnumConstantNotFoundException) {
              reader.addUnknownField(tag, FieldEncoding.VARINT, e.value.toLong())
            }
            2 -> data = ProtoAdapter.STRING.decode(reader)
            3 -> operands.add(Condition.ADAPTER.decode(reader))
            else -> reader.readUnknownField(tag)
          }
        }
        return Condition(
          type = type,
          data = data,
          operands = operands,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: Condition): Condition = value.copy(
        operands = value.operands.redactElements(Condition.ADAPTER),
        unknownFields = ByteString.EMPTY
      )
    }
  }

  enum class Type(
    override val value: Int
  ) : WireEnum {
    TRUE(0),

    FALSE(1),

    AND(2),

    OR(3),

    NOT(4),

    HAS_COURSE(5),

    HAS_LABEL(6);

    companion object {
      @JvmField
      val ADAPTER: ProtoAdapter<Type> = object : EnumAdapter<Type>(
        Type::class
      ) {
        override fun fromValue(value: Int): Type? = Type.fromValue(value)
      }

      @JvmStatic
      fun fromValue(value: Int): Type? = when (value) {
        0 -> TRUE
        1 -> FALSE
        2 -> AND
        3 -> OR
        4 -> NOT
        5 -> HAS_COURSE
        6 -> HAS_LABEL
        else -> null
      }
    }
  }
}
