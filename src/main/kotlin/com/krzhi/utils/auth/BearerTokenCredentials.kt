package com.krzhi.utils.auth

import io.grpc.CallCredentials
import io.grpc.Metadata
import io.grpc.Status
import java.util.concurrent.Executor

class BearerTokenCredentials(private val token: String) : CallCredentials() {
    override fun applyRequestMetadata(
        requestInfo: RequestInfo?,
        appExecutor: Executor,
        applier: MetadataApplier
    ) {
        appExecutor.execute {
            try {
                val headers = Metadata()
                headers.put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer $token"
                )
                applier.apply(headers)
            } catch (e: Throwable) {
                applier.fail(Status.UNAUTHENTICATED.withCause(e))
            }
        }
    }
}