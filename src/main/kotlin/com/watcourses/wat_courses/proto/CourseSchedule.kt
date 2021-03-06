// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: uwapi.proto
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
import kotlin.Float
import kotlin.Int
import kotlin.Nothing
import kotlin.String
import kotlin.collections.List
import kotlin.hashCode
import kotlin.jvm.JvmField
import okio.ByteString

class CourseSchedule(
  /**
   * Requested subject acronym
   */
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val subject: String? = null,
  /**
   * Registrar assigned class number
   */
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val catalogNumber: String? = null,
  /**
   * Credit count for the mentioned course
   */
  @field:WireField(
    tag = 3,
    adapter = "com.squareup.wire.ProtoAdapter#FLOAT"
  )
  val units: Float? = null,
  /**
   * Class name and title
   */
  @field:WireField(
    tag = 4,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val title: String? = null,
  /**
   * Additional notes regarding enrollment for the given term
   */
  @field:WireField(
    tag = 5,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val note: String? = null,
  /**
   * Associated term specific class enrollment number
   */
  @field:WireField(
    tag = 6,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val classNumber: Int? = null,
  /**
   * Class instruction and number
   */
  @field:WireField(
    tag = 7,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val section: String? = null,
  /**
   * Name of the campus the course is being offered
   */
  @field:WireField(
    tag = 8,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val campus: String? = null,
  /**
   * Associated class id
   */
  @field:WireField(
    tag = 9,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val associatedClass: Int? = null,
  /**
   * Name of the related course component
   */
  @field:WireField(
    tag = 10,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val relatedComponent1: String? = null,
  /**
   * Name of the second related course component
   */
  @field:WireField(
    tag = 11,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val relatedComponent2: String? = null,
  /**
   * Class enrollment capacity
   */
  @field:WireField(
    tag = 12,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val enrollmentCapacity: Int? = null,
  /**
   * Total current class enrollment
   */
  @field:WireField(
    tag = 13,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val enrollmentTotal: Int? = null,
  /**
   * Class waiting capacity
   */
  @field:WireField(
    tag = 14,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val waitingCapacity: Int? = null,
  /**
   * Total current waiting students
   */
  @field:WireField(
    tag = 15,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val waitingTotal: Int? = null,
  /**
   * Class discussion topic
   */
  @field:WireField(
    tag = 16,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val topic: String? = null,
  /**
   * Course specific enrollment reservation data
   */
  @field:WireField(
    tag = 17,
    adapter = "com.watcourses.wat_courses.proto.Reserve#ADAPTER",
    label = WireField.Label.REPEATED
  )
  val reserves: List<Reserve> = emptyList(),
  /**
   * Schedule data
   */
  @field:WireField(
    tag = 18,
    adapter = "com.watcourses.wat_courses.proto.Class#ADAPTER",
    label = WireField.Label.REPEATED
  )
  val classes: List<Class> = emptyList(),
  /**
   * A list of classes the course is held with
   */
  @field:WireField(
    tag = 19,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.REPEATED
  )
  val heldWith: List<String> = emptyList(),
  /**
   * 4 digit term representation
   */
  @field:WireField(
    tag = 20,
    adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  val term: Int? = null,
  /**
   * Undergraduate or graduate course classification optional string lastUpdated = 22; // ISO8601
   * timestamp of when the data was last updated
   */
  @field:WireField(
    tag = 21,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val academicLevel: String? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<CourseSchedule, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is CourseSchedule) return false
    return unknownFields == other.unknownFields
        && subject == other.subject
        && catalogNumber == other.catalogNumber
        && units == other.units
        && title == other.title
        && note == other.note
        && classNumber == other.classNumber
        && section == other.section
        && campus == other.campus
        && associatedClass == other.associatedClass
        && relatedComponent1 == other.relatedComponent1
        && relatedComponent2 == other.relatedComponent2
        && enrollmentCapacity == other.enrollmentCapacity
        && enrollmentTotal == other.enrollmentTotal
        && waitingCapacity == other.waitingCapacity
        && waitingTotal == other.waitingTotal
        && topic == other.topic
        && reserves == other.reserves
        && classes == other.classes
        && heldWith == other.heldWith
        && term == other.term
        && academicLevel == other.academicLevel
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + subject.hashCode()
      result = result * 37 + catalogNumber.hashCode()
      result = result * 37 + units.hashCode()
      result = result * 37 + title.hashCode()
      result = result * 37 + note.hashCode()
      result = result * 37 + classNumber.hashCode()
      result = result * 37 + section.hashCode()
      result = result * 37 + campus.hashCode()
      result = result * 37 + associatedClass.hashCode()
      result = result * 37 + relatedComponent1.hashCode()
      result = result * 37 + relatedComponent2.hashCode()
      result = result * 37 + enrollmentCapacity.hashCode()
      result = result * 37 + enrollmentTotal.hashCode()
      result = result * 37 + waitingCapacity.hashCode()
      result = result * 37 + waitingTotal.hashCode()
      result = result * 37 + topic.hashCode()
      result = result * 37 + reserves.hashCode()
      result = result * 37 + classes.hashCode()
      result = result * 37 + heldWith.hashCode()
      result = result * 37 + term.hashCode()
      result = result * 37 + academicLevel.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (subject != null) result += """subject=${sanitize(subject)}"""
    if (catalogNumber != null) result += """catalogNumber=${sanitize(catalogNumber)}"""
    if (units != null) result += """units=$units"""
    if (title != null) result += """title=${sanitize(title)}"""
    if (note != null) result += """note=${sanitize(note)}"""
    if (classNumber != null) result += """classNumber=$classNumber"""
    if (section != null) result += """section=${sanitize(section)}"""
    if (campus != null) result += """campus=${sanitize(campus)}"""
    if (associatedClass != null) result += """associatedClass=$associatedClass"""
    if (relatedComponent1 != null) result += """relatedComponent1=${sanitize(relatedComponent1)}"""
    if (relatedComponent2 != null) result += """relatedComponent2=${sanitize(relatedComponent2)}"""
    if (enrollmentCapacity != null) result += """enrollmentCapacity=$enrollmentCapacity"""
    if (enrollmentTotal != null) result += """enrollmentTotal=$enrollmentTotal"""
    if (waitingCapacity != null) result += """waitingCapacity=$waitingCapacity"""
    if (waitingTotal != null) result += """waitingTotal=$waitingTotal"""
    if (topic != null) result += """topic=${sanitize(topic)}"""
    if (reserves.isNotEmpty()) result += """reserves=$reserves"""
    if (classes.isNotEmpty()) result += """classes=$classes"""
    if (heldWith.isNotEmpty()) result += """heldWith=${sanitize(heldWith)}"""
    if (term != null) result += """term=$term"""
    if (academicLevel != null) result += """academicLevel=${sanitize(academicLevel)}"""
    return result.joinToString(prefix = "CourseSchedule{", separator = ", ", postfix = "}")
  }

  fun copy(
    subject: String? = this.subject,
    catalogNumber: String? = this.catalogNumber,
    units: Float? = this.units,
    title: String? = this.title,
    note: String? = this.note,
    classNumber: Int? = this.classNumber,
    section: String? = this.section,
    campus: String? = this.campus,
    associatedClass: Int? = this.associatedClass,
    relatedComponent1: String? = this.relatedComponent1,
    relatedComponent2: String? = this.relatedComponent2,
    enrollmentCapacity: Int? = this.enrollmentCapacity,
    enrollmentTotal: Int? = this.enrollmentTotal,
    waitingCapacity: Int? = this.waitingCapacity,
    waitingTotal: Int? = this.waitingTotal,
    topic: String? = this.topic,
    reserves: List<Reserve> = this.reserves,
    classes: List<Class> = this.classes,
    heldWith: List<String> = this.heldWith,
    term: Int? = this.term,
    academicLevel: String? = this.academicLevel,
    unknownFields: ByteString = this.unknownFields
  ): CourseSchedule = CourseSchedule(subject, catalogNumber, units, title, note, classNumber,
      section, campus, associatedClass, relatedComponent1, relatedComponent2, enrollmentCapacity,
      enrollmentTotal, waitingCapacity, waitingTotal, topic, reserves, classes, heldWith, term,
      academicLevel, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<CourseSchedule> = object : ProtoAdapter<CourseSchedule>(
      FieldEncoding.LENGTH_DELIMITED, 
      CourseSchedule::class, 
      "type.googleapis.com/com.watcourses.wat_courses.proto.CourseSchedule"
    ) {
      override fun encodedSize(value: CourseSchedule): Int = 
        ProtoAdapter.STRING.encodedSizeWithTag(1, value.subject) +
        ProtoAdapter.STRING.encodedSizeWithTag(2, value.catalogNumber) +
        ProtoAdapter.FLOAT.encodedSizeWithTag(3, value.units) +
        ProtoAdapter.STRING.encodedSizeWithTag(4, value.title) +
        ProtoAdapter.STRING.encodedSizeWithTag(5, value.note) +
        ProtoAdapter.INT32.encodedSizeWithTag(6, value.classNumber) +
        ProtoAdapter.STRING.encodedSizeWithTag(7, value.section) +
        ProtoAdapter.STRING.encodedSizeWithTag(8, value.campus) +
        ProtoAdapter.INT32.encodedSizeWithTag(9, value.associatedClass) +
        ProtoAdapter.STRING.encodedSizeWithTag(10, value.relatedComponent1) +
        ProtoAdapter.STRING.encodedSizeWithTag(11, value.relatedComponent2) +
        ProtoAdapter.INT32.encodedSizeWithTag(12, value.enrollmentCapacity) +
        ProtoAdapter.INT32.encodedSizeWithTag(13, value.enrollmentTotal) +
        ProtoAdapter.INT32.encodedSizeWithTag(14, value.waitingCapacity) +
        ProtoAdapter.INT32.encodedSizeWithTag(15, value.waitingTotal) +
        ProtoAdapter.STRING.encodedSizeWithTag(16, value.topic) +
        Reserve.ADAPTER.asRepeated().encodedSizeWithTag(17, value.reserves) +
        Class.ADAPTER.asRepeated().encodedSizeWithTag(18, value.classes) +
        ProtoAdapter.STRING.asRepeated().encodedSizeWithTag(19, value.heldWith) +
        ProtoAdapter.INT32.encodedSizeWithTag(20, value.term) +
        ProtoAdapter.STRING.encodedSizeWithTag(21, value.academicLevel) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: CourseSchedule) {
        ProtoAdapter.STRING.encodeWithTag(writer, 1, value.subject)
        ProtoAdapter.STRING.encodeWithTag(writer, 2, value.catalogNumber)
        ProtoAdapter.FLOAT.encodeWithTag(writer, 3, value.units)
        ProtoAdapter.STRING.encodeWithTag(writer, 4, value.title)
        ProtoAdapter.STRING.encodeWithTag(writer, 5, value.note)
        ProtoAdapter.INT32.encodeWithTag(writer, 6, value.classNumber)
        ProtoAdapter.STRING.encodeWithTag(writer, 7, value.section)
        ProtoAdapter.STRING.encodeWithTag(writer, 8, value.campus)
        ProtoAdapter.INT32.encodeWithTag(writer, 9, value.associatedClass)
        ProtoAdapter.STRING.encodeWithTag(writer, 10, value.relatedComponent1)
        ProtoAdapter.STRING.encodeWithTag(writer, 11, value.relatedComponent2)
        ProtoAdapter.INT32.encodeWithTag(writer, 12, value.enrollmentCapacity)
        ProtoAdapter.INT32.encodeWithTag(writer, 13, value.enrollmentTotal)
        ProtoAdapter.INT32.encodeWithTag(writer, 14, value.waitingCapacity)
        ProtoAdapter.INT32.encodeWithTag(writer, 15, value.waitingTotal)
        ProtoAdapter.STRING.encodeWithTag(writer, 16, value.topic)
        Reserve.ADAPTER.asRepeated().encodeWithTag(writer, 17, value.reserves)
        Class.ADAPTER.asRepeated().encodeWithTag(writer, 18, value.classes)
        ProtoAdapter.STRING.asRepeated().encodeWithTag(writer, 19, value.heldWith)
        ProtoAdapter.INT32.encodeWithTag(writer, 20, value.term)
        ProtoAdapter.STRING.encodeWithTag(writer, 21, value.academicLevel)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): CourseSchedule {
        var subject: String? = null
        var catalogNumber: String? = null
        var units: Float? = null
        var title: String? = null
        var note: String? = null
        var classNumber: Int? = null
        var section: String? = null
        var campus: String? = null
        var associatedClass: Int? = null
        var relatedComponent1: String? = null
        var relatedComponent2: String? = null
        var enrollmentCapacity: Int? = null
        var enrollmentTotal: Int? = null
        var waitingCapacity: Int? = null
        var waitingTotal: Int? = null
        var topic: String? = null
        val reserves = mutableListOf<Reserve>()
        val classes = mutableListOf<Class>()
        val heldWith = mutableListOf<String>()
        var term: Int? = null
        var academicLevel: String? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> subject = ProtoAdapter.STRING.decode(reader)
            2 -> catalogNumber = ProtoAdapter.STRING.decode(reader)
            3 -> units = ProtoAdapter.FLOAT.decode(reader)
            4 -> title = ProtoAdapter.STRING.decode(reader)
            5 -> note = ProtoAdapter.STRING.decode(reader)
            6 -> classNumber = ProtoAdapter.INT32.decode(reader)
            7 -> section = ProtoAdapter.STRING.decode(reader)
            8 -> campus = ProtoAdapter.STRING.decode(reader)
            9 -> associatedClass = ProtoAdapter.INT32.decode(reader)
            10 -> relatedComponent1 = ProtoAdapter.STRING.decode(reader)
            11 -> relatedComponent2 = ProtoAdapter.STRING.decode(reader)
            12 -> enrollmentCapacity = ProtoAdapter.INT32.decode(reader)
            13 -> enrollmentTotal = ProtoAdapter.INT32.decode(reader)
            14 -> waitingCapacity = ProtoAdapter.INT32.decode(reader)
            15 -> waitingTotal = ProtoAdapter.INT32.decode(reader)
            16 -> topic = ProtoAdapter.STRING.decode(reader)
            17 -> reserves.add(Reserve.ADAPTER.decode(reader))
            18 -> classes.add(Class.ADAPTER.decode(reader))
            19 -> heldWith.add(ProtoAdapter.STRING.decode(reader))
            20 -> term = ProtoAdapter.INT32.decode(reader)
            21 -> academicLevel = ProtoAdapter.STRING.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return CourseSchedule(
          subject = subject,
          catalogNumber = catalogNumber,
          units = units,
          title = title,
          note = note,
          classNumber = classNumber,
          section = section,
          campus = campus,
          associatedClass = associatedClass,
          relatedComponent1 = relatedComponent1,
          relatedComponent2 = relatedComponent2,
          enrollmentCapacity = enrollmentCapacity,
          enrollmentTotal = enrollmentTotal,
          waitingCapacity = waitingCapacity,
          waitingTotal = waitingTotal,
          topic = topic,
          reserves = reserves,
          classes = classes,
          heldWith = heldWith,
          term = term,
          academicLevel = academicLevel,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: CourseSchedule): CourseSchedule = value.copy(
        reserves = value.reserves.redactElements(Reserve.ADAPTER),
        classes = value.classes.redactElements(Class.ADAPTER),
        unknownFields = ByteString.EMPTY
      )
    }
  }
}
