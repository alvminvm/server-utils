package com.krzhi.utils.auth

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationFilter(private val jwt: Jwt) : Filter {
    override fun init(filterConfig: FilterConfig?) {
        // 初始化操作，可选
    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val token = httpRequest.getHeader("Authorization")?.removePrefix("Bearer ") ?: ""
        if (token.isBlank()) {
            // 返回 401
            (response as? HttpServletResponse)?.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }

        // 获取token并验证
        val info = jwt.parseAuthToken(token)
        if (info == null) {
            (response as? HttpServletResponse)?.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }

        val authentication = UsernamePasswordAuthenticationToken(info, null, listOf())
        authentication.details = WebAuthenticationDetailsSource().buildDetails(httpRequest)
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(request, response)
    }

    override fun destroy() {
        // 销毁操作，可选
    }
}