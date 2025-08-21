package com.krzhi.utils

data class Result(
    var code: Int,
    var msg: String,
    var data: Any? = null
) {
    companion object {
        fun success(data: Any? = null): Result {
            return Result(0, "success", data)
        }

        fun fail(msg: String): Result {
            return Result(-1, msg)
        }
    }
}
