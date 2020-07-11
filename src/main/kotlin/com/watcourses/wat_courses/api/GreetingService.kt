package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.proto.GreeterServer
import com.watcourses.wat_courses.proto.HelloReply
import com.watcourses.wat_courses.proto.HelloRequest
import org.lognet.springboot.grpc.GRpcService

@GRpcService
class GreeterService: GreeterServer {
    override suspend fun SayHello(request: HelloRequest): HelloReply {
        return HelloReply("hello")
    }
}