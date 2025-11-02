package com.krzhi.utils

import okhttp3.ConnectionPool
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

private val pool = ConnectionPool(10, 5, TimeUnit.MINUTES)

val mediaType = "application/json".toMediaTypeOrNull()

val httpClient = OkHttpClient.Builder()
    .readTimeout(15, TimeUnit.SECONDS)
    .writeTimeout(15, TimeUnit.SECONDS)
    .connectTimeout(15, TimeUnit.SECONDS)
    .connectionPool(pool)
    .build()
