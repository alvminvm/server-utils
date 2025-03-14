package com.krzhi.utils.auth

import io.grpc.*
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order

/**
 * grpc认证拦截器
 * 使用方式：Application 增加 scanBasePackages = ["com.krzhi.utils.auth"]
 */
@Order(100)
@GrpcGlobalServerInterceptor
class GrpcAuthInterceptor: ServerInterceptor {
    private val log = LoggerFactory.getLogger(GrpcAuthInterceptor::class.java)

    @Autowired private lateinit var context: AuthContext

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        var ctx = context.auth(headers)
        if (ctx == null) {
            call.close(Status.UNAUTHENTICATED, headers)
            return object : ServerCall.Listener<ReqT>(){}
        }

        ctx = context.scope(headers, ctx)

        return Contexts.interceptCall(ctx, call, headers, next)
    }
}