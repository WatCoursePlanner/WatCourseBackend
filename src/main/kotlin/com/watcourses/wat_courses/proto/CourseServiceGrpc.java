package com.watcourses.wat_courses.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.1)",
    comments = "Source: grpc.proto")
public final class CourseServiceGrpc {

  private CourseServiceGrpc() {}

  public static final String SERVICE_NAME = "com.watcourses.wat_courses.proto.CourseService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest,
      com.watcourses.wat_courses.proto.Courses.CourseInfo> getGetCourseInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getCourseInfo",
      requestType = com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest.class,
      responseType = com.watcourses.wat_courses.proto.Courses.CourseInfo.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest,
      com.watcourses.wat_courses.proto.Courses.CourseInfo> getGetCourseInfoMethod() {
    io.grpc.MethodDescriptor<com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest, com.watcourses.wat_courses.proto.Courses.CourseInfo> getGetCourseInfoMethod;
    if ((getGetCourseInfoMethod = CourseServiceGrpc.getGetCourseInfoMethod) == null) {
      synchronized (CourseServiceGrpc.class) {
        if ((getGetCourseInfoMethod = CourseServiceGrpc.getGetCourseInfoMethod) == null) {
          CourseServiceGrpc.getGetCourseInfoMethod = getGetCourseInfoMethod = 
              io.grpc.MethodDescriptor.<com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest, com.watcourses.wat_courses.proto.Courses.CourseInfo>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.watcourses.wat_courses.proto.CourseService", "getCourseInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.watcourses.wat_courses.proto.Courses.CourseInfo.getDefaultInstance()))
                  .setSchemaDescriptor(new CourseServiceMethodDescriptorSupplier("getCourseInfo"))
                  .build();
          }
        }
     }
     return getGetCourseInfoMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CourseServiceStub newStub(io.grpc.Channel channel) {
    return new CourseServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CourseServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new CourseServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CourseServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new CourseServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class CourseServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getCourseInfo(com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest request,
        io.grpc.stub.StreamObserver<com.watcourses.wat_courses.proto.Courses.CourseInfo> responseObserver) {
      asyncUnimplementedUnaryCall(getGetCourseInfoMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetCourseInfoMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest,
                com.watcourses.wat_courses.proto.Courses.CourseInfo>(
                  this, METHODID_GET_COURSE_INFO)))
          .build();
    }
  }

  /**
   */
  public static final class CourseServiceStub extends io.grpc.stub.AbstractStub<CourseServiceStub> {
    private CourseServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CourseServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CourseServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CourseServiceStub(channel, callOptions);
    }

    /**
     */
    public void getCourseInfo(com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest request,
        io.grpc.stub.StreamObserver<com.watcourses.wat_courses.proto.Courses.CourseInfo> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetCourseInfoMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class CourseServiceBlockingStub extends io.grpc.stub.AbstractStub<CourseServiceBlockingStub> {
    private CourseServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CourseServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CourseServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CourseServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.watcourses.wat_courses.proto.Courses.CourseInfo getCourseInfo(com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetCourseInfoMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class CourseServiceFutureStub extends io.grpc.stub.AbstractStub<CourseServiceFutureStub> {
    private CourseServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CourseServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CourseServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CourseServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.watcourses.wat_courses.proto.Courses.CourseInfo> getCourseInfo(
        com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetCourseInfoMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_COURSE_INFO = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CourseServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CourseServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_COURSE_INFO:
          serviceImpl.getCourseInfo((com.watcourses.wat_courses.proto.Grpc.CourseInfoRequest) request,
              (io.grpc.stub.StreamObserver<com.watcourses.wat_courses.proto.Courses.CourseInfo>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class CourseServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CourseServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.watcourses.wat_courses.proto.Grpc.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CourseService");
    }
  }

  private static final class CourseServiceFileDescriptorSupplier
      extends CourseServiceBaseDescriptorSupplier {
    CourseServiceFileDescriptorSupplier() {}
  }

  private static final class CourseServiceMethodDescriptorSupplier
      extends CourseServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CourseServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CourseServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CourseServiceFileDescriptorSupplier())
              .addMethod(getGetCourseInfoMethod())
              .build();
        }
      }
    }
    return result;
  }
}
