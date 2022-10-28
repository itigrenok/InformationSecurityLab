package ru.stankin.dp.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.stankin.dp.dto.ParamsModel
import ru.stankin.dp.service.LoginService

@Controller
class LoginController(
    private val loginService: LoginService
) {

    @PostMapping("/changePassword")
    fun changePassword(model: Model, @RequestParam login: String, @RequestParam oldPassword: String, @RequestParam firstNewPassword: String, @RequestParam secondNewPassword: String): String {
        try {
            val firstChangePassword = loginService.changePassword(login, oldPassword, firstNewPassword, secondNewPassword)

            return if (firstChangePassword) "redirect:/logout" else "redirect:/user"
        } catch (e: Exception) {
            model.addAttribute("params", ParamsModel(true, e.message!!))

            return "changePassword"
        }
    }
}