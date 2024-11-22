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
