package com.watcourses.wat_courses.api

import com.watcourses.wat_courses.proto.CourseInfoResponse
import com.watcourses.wat_courses.proto.CourseServiceGrpc.*

import io.*
import io.grpc.*
import io.grpc.stub.ServerCalls
import io.grpc.stub.StreamObserver
import io.rouz.*
import io.rouz.grpc.ContextCoroutineContextElement

import kotlin.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*



@javax.annotation.Generated(
        value = ["by gRPC Kotlin generator"],
        comments = "Source: grpc.proto"
)
abstract class CourseServiceImplBase(
        coroutineContext: CoroutineContext = Dispatchers.Default
) : BindableService, CoroutineScope {

    private val _coroutineContext: CoroutineContext = coroutineContext

    override val coroutineContext: CoroutineContext
        get() = ContextCoroutineContextElement() + _coroutineContext




    open suspend fun getCourseInfo(request: com.watcourses.wat_courses.proto.CourseInfoRequest): com.watcourses.wat_courses.proto.CourseInfoResponse {
        throw unimplemented(getGetCourseInfoMethod()).asRuntimeException()
    }

    internal fun getCourseInfoInternal(
            request: com.watcourses.wat_courses.proto.CourseInfoRequest,
            responseObserver: StreamObserver<CourseInfoResponse>
    ) {
        launch {
            tryCatchingStatus(responseObserver) {
                val response = getCourseInfo(request)
                onNext(response)
            }
        }
    }

    override fun bindService(): ServerServiceDefinition {
        return ServerServiceDefinition.builder(getServiceDescriptor())
                .addMethod(
                        getGetCourseInfoMethod(),
                        ServerCalls.asyncUnaryCall(
                                MethodHandlers(METHODID_GET_COURSE_INFO)
                        )
                )
                .build()
    }

    private fun unimplemented(methodDescriptor: MethodDescriptor<*, *>): Status {
        return Status.UNIMPLEMENTED
                .withDescription("Method ${methodDescriptor.fullMethodName} is unimplemented")
    }

    private fun <E> handleException(t: Throwable?, responseObserver: StreamObserver<E>) {
        when (t) {
            null -> return
            is CancellationException -> handleException(t.cause, responseObserver)
            is StatusException, is StatusRuntimeException -> responseObserver.onError(t)
            is RuntimeException -> {
                responseObserver.onError(Status.UNKNOWN.asRuntimeException())
                throw t
            }
            is Exception -> {
                responseObserver.onError(Status.UNKNOWN.asException())
                throw t
            }
            else -> {
                responseObserver.onError(Status.INTERNAL.asException())
                throw t
            }
        }
    }

    private suspend fun <E> tryCatchingStatus(responseObserver: StreamObserver<E>, body: suspend StreamObserver<E>.() -> Unit) {
        try {
            responseObserver.body()
            responseObserver.onCompleted()
        } catch (t: Throwable) {
            handleException(t, responseObserver)
        }
    }

    private val METHODID_GET_COURSE_INFO = 0

    private inner class MethodHandlers<Req, Resp> internal constructor(
            private val methodId: Int
    ) : ServerCalls.UnaryMethod<Req, Resp>,
            ServerCalls.ServerStreamingMethod<Req, Resp>,
            ServerCalls.ClientStreamingMethod<Req, Resp>,
            ServerCalls.BidiStreamingMethod<Req, Resp> {

        @Suppress("UNCHECKED_CAST")
        override fun invoke(request: Req, responseObserver: StreamObserver<Resp>) {
            when (methodId) {
                METHODID_GET_COURSE_INFO ->
                    this@CourseServiceImplBase.getCourseInfoInternal(
                            request as com.watcourses.wat_courses.proto.CourseInfoRequest,
                            responseObserver as StreamObserver<com.watcourses.wat_courses.proto.CourseInfoResponse>
                    )
                else -> throw AssertionError()
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun invoke(responseObserver: StreamObserver<Resp>): StreamObserver<Req> {
            when (methodId) {
                else -> throw AssertionError()
            }
        }
    }
}
