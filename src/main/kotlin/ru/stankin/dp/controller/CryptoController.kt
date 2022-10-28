package ru.stankin.dp.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.stankin.dp.service.CryptoService
import ru.stankin.dp.service.FileService
import ru.stankin.dp.service.LoginService
import ru.stankin.dp.service.UserRepository

@Controller
class CryptoController(
    private val fileService: FileService,
    private val loginService: LoginService,
    private val userRepository: UserRepository,
    private val cryptoService: CryptoService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(CryptoController::class.java)
    }

    @PostMapping("/cipher")
    fun fileDecryption(model: Model, @RequestParam password: String): String {
        try {
            cryptoService.validPassword(password)

            fileService.createTriggerFile()
            fileService.openDataFile()
            userRepository.findAllFileUsers().forEach { loginService.addUser(it) }
        } catch (e: Exception) {
            model.addAttribute("error", e.message)

            return "/cipher"
        }

        return "redirect:/login"
    }

    @GetMapping("/cipher")
    fun cipherPage(): String {
        return "cipher"
    }
}