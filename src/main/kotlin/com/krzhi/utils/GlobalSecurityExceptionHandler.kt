package com.krzhi.utils

import com.krzhi.utils.dto.Result
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalSecurityExceptionHandler {
    // 捕获权限不足异常
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException, response: HttpServletResponse): Result<*> {
        response.status = HttpServletResponse.SC_FORBIDDEN
        return Result.denied<Unit>()
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAccessDenied(e: AuthorizationDeniedException, response: HttpServletResponse): Result<*> {
        response.status = HttpServletResponse.SC_FORBIDDEN
        return Result.denied<Unit>()
    }

}