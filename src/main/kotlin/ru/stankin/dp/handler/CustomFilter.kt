package ru.stankin.dp.handler

import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_TEMPORARY_REDIRECT
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import ru.stankin.dp.service.CryptoService
import ru.stankin.dp.service.FileService
import ru.stankin.dp.service.UserRepository

@Component
class CustomFilter(
    private val userRepository: UserRepository,
    private val fileService: FileService,
    private val cryptoService: CryptoService
) : GenericFilterBean() {

    companion object {
        private val logger = LoggerFactory.getLogger(CustomFilter::class.java)

        private val notRedirectsUrl = listOf("/changePassword", "/cipher", "/fileDecryption")
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        redirectToCipherPassword(request as HttpServletRequest, response as HttpServletResponse)
        redirectChangePassword(request, response)

        chain.doFilter(request, response)
    }

    private fun redirectChangePassword(request: HttpServletRequest, response: HttpServletResponse) {
        if (request.requestURI == "/changePassword") {
            return
        }

        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication?.principal is UserDetails) {
            val user = authentication.principal as UserDetails

            userRepository.findFileUser(user.username)?.takeIf { it.transportPassword }?.let {
                response.status = SC_TEMPORARY_REDIRECT
                response.setHeader("Location", request.contextPath + "/changePassword")
            }
        }
    }

    private fun redirectToCipherPassword(request: HttpServletRequest, response: HttpServletResponse) {
        if (!fileService.existTriggerFile() && !notRedirectsUrl.contains(request.requestURI)) {
            response.status = SC_TEMPORARY_REDIRECT
            response.setHeader("Location", request.contextPath + "/cipher")
        }
    }
}