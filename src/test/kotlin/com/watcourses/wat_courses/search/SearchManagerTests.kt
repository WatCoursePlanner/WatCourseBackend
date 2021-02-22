package com.watcourses.wat_courses.search

import com.watcourses.wat_courses.Utils
import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.proto.SearchCourseRequest
import com.watcourses.wat_courses.proto.Sort
import com.watcourses.wat_courses.utils.CachedData
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

    @Test
    fun `results are properly sorted`() {
        cachedData.invalidateAllCourses()

        utils.createSingleCourse(code = "CS 1", name = "B")
        utils.createSingleCourse(code = "CS 2", name = "C")
        utils.createSingleCourse(code = "CS 3", name = "A")

        // by code asc
        var request = SearchCourseRequest(
            searchQuery = "",
            sort = Sort(
                sortBy = Sort.SortBy.CODE,
                order = Sort.Order.ASC,
            ),
        )
        var paginatedResults = searchManager.search(request).courses
        assertThat(paginatedResults.map { it.code }).containsExactly(
            "CS 1",
            "CS 2",
            "CS 3",
        )

        // by code desc
        request = SearchCourseRequest(
            searchQuery = "",
            sort = Sort(
                sortBy = Sort.SortBy.CODE,
                order = Sort.Order.DESC,
            ),
        )
        paginatedResults = searchManager.search(request).courses
        assertThat(paginatedResults.map { it.code }).containsExactly(
            "CS 3",
            "CS 2",
            "CS 1",
        )

        // by name desc
        request = SearchCourseRequest(
            searchQuery = "",
            sort = Sort(
                sortBy = Sort.SortBy.TITLE,
                order = Sort.Order.DESC,
            ),
        )
        paginatedResults = searchManager.search(request).courses
        assertThat(paginatedResults.map { it.name }).containsExactly(
            "C",
            "B",
            "A",
        )
    }
}
