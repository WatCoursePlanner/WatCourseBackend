package com.watcourses.wat_courses.search

import com.watcourses.wat_courses.Utils
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.proto.CourseInfo
import com.watcourses.wat_courses.proto.SearchCourseRequest
import com.watcourses.wat_courses.proto.Sort
import com.watcourses.wat_courses.utils.CachedData
import com.watcourses.wat_courses.utils.CourseBuilderProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class SearchManagerTests {
    @Autowired
    private lateinit var searchManager: SearchManager

    @Autowired
    private lateinit var utils: Utils

    @Autowired
    private lateinit var cachedData: CachedData

    @Autowired
    private lateinit var courseBuilderProvider: CourseBuilderProvider

    @Test
    fun `results are properly sorted`() {
        cachedData.invalidateAllCourses()

        courseBuilderProvider.get().code("CS 1").name("B").liked(0.5).build()
        courseBuilderProvider.get().code("CS 2").name("C").liked(0.8).build()
        courseBuilderProvider.get().code("CS 3").name("A").liked(null).build()

        // by code asc
        assertThat(search("", Sort.SortBy.CODE, Sort.Order.ASC).map { it.code })
            .containsExactly("CS 1", "CS 2", "CS 3")

        // by code desc
        assertThat(search("", Sort.SortBy.CODE, Sort.Order.DESC).map { it.code })
            .containsExactly("CS 3", "CS 2", "CS 1")

        // by name desc
        assertThat(search("", Sort.SortBy.TITLE, Sort.Order.DESC).map { it.name })
            .containsExactly("C", "B", "A")

        // by liked desc
        assertThat(search("", Sort.SortBy.LIKED, Sort.Order.DESC).map { it.liked })
            .containsExactly(0.8, 0.5, null)
    }

    @Test
    fun `basic content filter`() {
        cachedData.invalidateAllCourses()

        courseBuilderProvider.get().code("CS 1").name("1A1").description("d 1").build()
        courseBuilderProvider.get().code("CS 2").name("0B0").description("d 12").build()
        courseBuilderProvider.get().code("CS 3").name("0D0").description("d 21").build()

        // match name
        assertThat(search("A").map { it.code }).containsExactlyInAnyOrder("CS 1")
        // match code and description
        assertThat(search("2").map { it.code }).containsExactlyInAnyOrder("CS 2", "CS 3")
        // match code
        assertThat(search("CS 3").map { it.code }).containsExactlyInAnyOrder("CS 3")
        // match description
        assertThat(search("d").map { it.code }).containsExactlyInAnyOrder("CS 1", "CS 2", "CS 3")
        // match name case insensitively
        assertThat(search("b").map { it.code }).containsExactlyInAnyOrder("CS 2")
        // multi part match - CS 2 has 2 in its code and B in its name, other two doesn't have B
        assertThat(search("2 b").map { it.code }).containsExactlyInAnyOrder("CS 2")
    }

    private fun search(
        query: String,
        sortBy: Sort.SortBy? = null,
        order: Sort.Order? = null
    ): List<CourseInfo> {
        return searchManager.search(
            SearchCourseRequest(
                searchQuery = query,
                sort = Sort(
                    sortBy = sortBy,
                    order = order,
                )
            )
        ).courses
    }
}
