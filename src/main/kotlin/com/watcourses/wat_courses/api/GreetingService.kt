package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.proto.CourseServiceImplBase
import com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest
import com.watcourses.wat_courses.proto.Courses.*
import kotlinx.coroutines.*
import org.lognet.springboot.grpc.GRpcService
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.Executors.newFixedThreadPool

@GRpcService
class CourseService : CourseServiceImplBase(
        coroutineContext = newFixedThreadPool(4).asCoroutineDispatcher()
) {

    override suspend fun getCourseInfo(request: CourseInfoRequest): CourseInfo {
        val dbCourseRepo: DbCourseRepo
        return dbCourseRepo.findByCode(request.code)!!.toProto()
    }
}