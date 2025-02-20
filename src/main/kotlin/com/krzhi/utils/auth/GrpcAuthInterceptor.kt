package com.krzhi.utils.auth

import io.grpc.*
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

@GrpcGlobalServerInterceptor
class GrpcAuthInterceptor: ServerInterceptor {
    private val log = LoggerFactory.getLogger(GrpcAuthInterceptor::class.java)

    @Autowired private lateinit var context: AuthContext

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        val ctx = context.auth(headers)
        if (ctx == null) {
            call.close(Status.UNAUTHENTICATED, headers)
            return object : ServerCall.Listener<ReqT>(){}
        }

        return Contexts.interceptCall(ctx, call, headers, next)
    }
}