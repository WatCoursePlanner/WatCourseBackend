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

    private fun pagination(results: List<CourseInfo>, pagination: PaginationInfoRequest?): SearchResult {
        val page = pagination?.zeroBasedPage ?: 0
        val size = pagination?.limit ?: 30
        return SearchResult(
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

    private fun <T> sortOrderedBy(
        list: List<T>,
        order: Sort.Order?,
        selector: (T) -> Comparable<*>?
    ): List<T> {
        return when (order ?: Sort.Order.ASC) {
            Sort.Order.ASC -> list.sortedWith(compareBy(selector))
            Sort.Order.DESC -> list.sortedWith(compareByDescending(selector))
        }
    }

    private fun sortResults(results: List<CourseInfo>, sort: Sort): List<CourseInfo> {
        return sortOrderedBy(
            list = results,
            order = sort.order,
            selector = when (sort.sortBy) {
                Sort.SortBy.TITLE -> { it -> it.name }
                Sort.SortBy.CODE -> { it -> it.code }
                Sort.SortBy.LIKE -> { it -> it.like }
                Sort.SortBy.EASY -> { it -> it.easy }
                Sort.SortBy.USEFUL -> { it -> it.useful }
                Sort.SortBy.RATING -> { it -> it.useful }
                else -> { it -> it.ratingsCount }
            }
        )
    }

    private fun search(courses: List<CourseInfo>, request: SearchCourseRequest): SearchResult {
        val result = filterResults(courses, request.searchQuery)
        val sortedResult = sortResults(result, request.sort ?: defaultSort())
        return pagination(sortedResult, request.pagination)
    }

    fun search(request: SearchCourseRequest): SearchResult {
        return search(cachedData.allCourses(), request)
    }

    class SearchResult(
        val courses: List<CourseInfo>,
        val paginationInfoResponse: PaginationInfoResponse,
    )
}