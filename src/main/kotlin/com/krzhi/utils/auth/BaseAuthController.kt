package com.krzhi.utils.auth

import org.springframework.security.core.context.SecurityContextHolder

abstract class BaseAuthController {
    val userAuthInfo: UserAuthInfo get() = userAuthInfoOrNull!!

    val userAuthInfoOrNull: UserAuthInfo?
        get() = SecurityContextHolder.getContext().authentication.principal as? UserAuthInfo
}