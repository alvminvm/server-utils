package com.krzhi.utils

data class Result(
    var code: Int,
    var msg: String,
    var data: Any? = null
) {
    init {
        // todo: 加密返回
    }

    companion object {
        fun success(data: Any? = null): Result {
            return Result(0, "success", data)
        }

        fun fail(msg: String): Result {
            return Result(-1, msg)
        }

        fun illegalArgs() = Result(-2, "参数有误")

        fun systemError() = Result(-3, "系统错误，请稍候重试")

        fun denied() = Result(-4, "无权限")
    }
}
