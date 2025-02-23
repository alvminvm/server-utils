package com.krzhi.utils

import io.grpc.Status
import io.grpc.stub.StreamObserver

fun <V> StreamObserver<V>.onCompleted(v: V) {
    onNext(v)
    onCompleted()
}

fun StreamObserver<*>.onError(status: Status, message: String? = null) {
    val t = (if (message.isNullOrBlank()) status else status.withDescription(message)).asRuntimeException()
    onError(t)
}

fun StreamObserver<*>.onUnauthenticated() = onError(Status.UNAUTHENTICATED, "请重新登录")

fun StreamObserver<*>.onInternal() = onError(Status.INTERNAL, "服务异常，请稍候")
