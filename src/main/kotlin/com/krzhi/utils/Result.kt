package com.krzhi.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

data class Result<T>(
    var code: Int,
    var msg: String,
    var data: T? = null
) {

    companion object {
        @OptIn(ExperimentalContracts::class)
        fun <T> Result<T>?.isSuccess(): Boolean {
            contract { returnsNotNull() }
            return this?.code == 0 && data != null
        }

        @OptIn(ExperimentalContracts::class)
        fun <T> Result<T>?.isFail() = !isSuccess()

        fun <T> success(data: T? = null): Result<T> {
            return Result(0, "success", data)
        }

        fun <T> fail(msg: String): Result<T> {
            return Result<T>(-1, msg)
        }

        fun <T> illegalArgs() = Result<T>(-2, "参数有误")

        fun <T> systemError() = Result<T>(-3, "系统错误，请稍候重试")

        fun <T> denied() = Result<T>(-4, "无权限")
    }
}
