package com.watcourses.wat_courses.search

import com.watcourses.wat_courses.proto.*
import com.watcourses.wat_courses.search.filters.SearchFilter
import com.watcourses.wat_courses.utils.CachedData
import org.springframework.stereotype.Component

@Component
class SearchManager(
    private val cachedData: CachedData,
    private val filters: List<SearchFilter>
) {
    private fun filterResults(courses: List<CourseInfo>, query: String?): List<CourseInfo> {
        if (query.isNullOrBlank()) return courses
        val queryParts = query.split(" ").map { it.trim() }
        return courses.filter { course ->
            queryParts.all { part -> // a course is considered matched if all queries are matched
                filters.any { filter -> // a course is considered matched if any filter matches it
                    filter.match(course, part)
                }
            }
        }
    }

    private fun pagination(results: List<CourseInfo>, pagination: PaginationInfoRequest?)
            : Pair<List<CourseInfo>, PaginationInfoResponse> {
        val page = pagination?.zeroBasedPage ?: 0
        val size = pagination?.limit ?: 30
        return Pair(
            results.drop(page * size).take(size),
            PaginationInfoResponse(
                totalPages = (results.size + size - 1) / size,
                limit = size,
                currentPage = page,
                totalResults = results.size
            )
        )
    }

    private fun defaultSort() = Sort(sortBy = Sort.SortBy.TITLE, order = Sort.Order.ASC)

    fun sortResults(results: List<CourseInfo>, sort: Sort): List<CourseInfo> {
        return results.sortedByDescending { it.ratingsCount }
    }

    fun search(request: SearchCourseRequest): Pair<List<CourseInfo>, PaginationInfoResponse> {
        val result = filterResults(cachedData.allCourses(), request.searchQuery)
        val sortedResult = sortResults(result, request.sort ?: defaultSort())
        return pagination(sortedResult, request.pagination)
    }
}