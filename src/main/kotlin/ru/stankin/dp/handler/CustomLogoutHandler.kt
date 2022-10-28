package ru.stankin.dp.handler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component
import ru.stankin.dp.enum.FileType
import ru.stankin.dp.service.CryptoService
import ru.stankin.dp.service.FileService

@Component
class CustomLogoutHandler(
    private val cryptoService: CryptoService,
    private val fileService: FileService
) : LogoutHandler {

    override fun logout(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        fileService.findByType(FileType.DATA)?.let {
            val cryptoText = cryptoService.fileEncryption(it)

            fileService.saveFile(cryptoText, fileService.findByType(FileType.CRYPTO_DATA))
            it.delete()
        }
    }
}