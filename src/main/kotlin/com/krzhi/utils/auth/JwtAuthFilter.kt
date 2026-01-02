package com.krzhi.utils.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter : OncePerRequestFilter() {

    @Autowired
    private lateinit var jwt: JwtService

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ") ?: ""
        if (token.isBlank()) {
            chain.doFilter(request, response)
            return
        }

        // 获取token并验证
        val info = jwt.parseAuthToken(token)
        if (info == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }

        val authorities = info.roles.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }
        val authentication = UsernamePasswordAuthenticationToken(info, token, authorities)
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(request, response)
    }
}