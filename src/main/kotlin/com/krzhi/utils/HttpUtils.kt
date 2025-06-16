package com.krzhi.utils

import com.google.gson.Gson
import okhttp3.ConnectionPool
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

/**
 * HTTP工具类
 */
object HttpUtils {
    private val gson = Gson()
    private val pool = ConnectionPool(10, 5, TimeUnit.MINUTES)

    private val mediaType = "application/json".toMediaTypeOrNull()

    private val client = OkHttpClient.Builder()
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .connectionPool(pool)
        .build()

    fun postJson(url: String, payload: Any, headers: Map<String, String> = mapOf()): String {
        val json = payload as? String ?: gson.toJson(payload)
        val body = json.toRequestBody(mediaType)

        val builder = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")

        headers.forEach { builder.addHeader(it.key, it.value) }

        val request = builder.build()

        try {
            client.newCall(request).execute().use { rsp ->
                return rsp.body?.string() ?: ""
            }
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    fun <T> postJson(url: String, payload: Any, type: Type, headers: Map<String, String> = mapOf()): T {
        val json = postJson(url, payload, headers)
        return gson.fromJson(json, type)
    }

    fun postText(url: String, body: String, headers: Map<String, String> = mapOf()): String {
        val builder = Request.Builder()
            .url(url)
            .post(body.toRequestBody("text/plain".toMediaTypeOrNull()))
            .addHeader("Content-Type", "text/plain;charset=UTF-8;")

        headers.forEach { builder.addHeader(it.key, it.value) }

        val request = builder.build()

        try {
            client.newCall(request).execute().use { rsp ->
                return rsp.body?.string() ?: ""
            }
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    fun <T> postText(url: String, body: String, type: Type, headers: Map<String, String> = mapOf()): T {
        val json = postText(url, body, headers)
        return gson.fromJson(json, type)
    }

}
