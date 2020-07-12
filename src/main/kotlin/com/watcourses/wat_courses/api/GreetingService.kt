package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.persistence.DbCourseRepo
import com.watcourses.wat_courses.proto.CourseInfoRequest
import com.watcourses.wat_courses.proto.CourseInfo
import kotlinx.coroutines.*
import org.lognet.springboot.grpc.GRpcService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.Executors.newFixedThreadPool

@GRpcService
class CourseService : CourseServiceImplBase(
        coroutineContext = newFixedThreadPool(4).asCoroutineDispatcher()
) {

    @Autowired
    private lateinit var dbCourseRepo: DbCourseRepo

    override suspend fun getCourseInfo(request: CourseInfoRequest): CourseInfo {
        return dbCourseRepo.findByCode(request.code)!!.toProto()
    }
}