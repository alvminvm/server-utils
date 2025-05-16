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
class HttpUtils {
    fun postJson(url: String, payload: Any): String {
        val json = payload as? String ?: gson.toJson(payload)
        val body = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { rsp ->
                return rsp.body?.string() ?: ""
            }
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    fun <T> postJson(url: String, payload: Any, type: Type): T {
        val json = postJson(url, payload)
        return gson.fromJson(json, type)
    }

    companion object {
        private val gson = Gson()
        private val pool = ConnectionPool(10, 5, TimeUnit.MINUTES)

        private val mediaType = "application/json".toMediaTypeOrNull()

        private val client = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .connectionPool(pool)
            .build()
    }
}
