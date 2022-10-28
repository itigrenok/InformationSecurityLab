package ru.stankin.dp.handler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component


@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {

    companion object {
        private val logger = LoggerFactory.getLogger(CustomAccessDeniedHandler::class.java)
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException?
    ) {
        SecurityContextHolder.getContext().authentication?.let {
            logger.info("User '{}' attempted to access the protected URL: {}", it.name, request.requestURI)
        }

        response.sendRedirect(request.contextPath + "/403")
    }
}