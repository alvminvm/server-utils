package com.krzhi.utils.auth

data class AuthInfo(
    val userId: Long,
    val createdAt: Long,
    val expiredAt: Long,
)
