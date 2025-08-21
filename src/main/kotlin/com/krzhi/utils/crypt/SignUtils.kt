package com.krzhi.utils.crypt

import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * 签名工具类
 */
object SignUtils {
    fun generateRaw(params: Map<String, String>): String {
        val treeMap = TreeMap(params)
        return treeMap.map { "${it.key}=${it.value}" }.joinToString("&")
    }

//    @JvmStatic
//    fun main(args: Array<String>) {
//        val gson = Gson()
//
//        val content: MutableMap<String, Any> = HashMap()
//        content["echo"] = "test"
//        content["version"] = "1.0"
//
//        val params: MutableMap<String, String> = HashMap()
//        params["version"] = "1.0"
//        params["sequence"] = "10000000001000000000100000000001"
//        params["timestamp"] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//        params["merchantNo"] = "AC20012024060601"
//        params["apiCode"] = "ping"
//        params["content"] = URLEncoder.encode(gson.toJson(content), StandardCharsets.UTF_8)
//        params["key"] = "12345678"
//        params["iv"] = "12345678"
//        var v = generateRaw(params)
//        println(v)
//
//        params.clear()
//        content.clear()
//
//        val data: MutableMap<String, Any> = HashMap()
//        data["echo"] = "test"
//        content["data"] = data
//        content["apiCode"] = "ping"
//        content["code"] = "00000"
//        content["message"] = "Ok"
//
//        params["sequence"] = "10000000001000000000100000000001"
//        params["timestamp"] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//        params["content"] = URLEncoder.encode(gson.toJson(content), StandardCharsets.UTF_8)
//        params["key"] = "12345678"
//        params["iv"] = "12345678"
//        v = generateRaw(params)
//        println(v)
//    }
}
