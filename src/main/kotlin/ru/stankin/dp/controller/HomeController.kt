package ru.stankin.dp.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import ru.stankin.dp.dto.ParamsModel
import ru.stankin.dp.mapper.UserMapper
import ru.stankin.dp.service.UserRepository

@Controller
class HomeController(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {

    @GetMapping("/")
    fun home(): String {
        return "user"
    }

    @GetMapping("/user")
    fun userPage(): String {
        return "user"
    }

    @GetMapping("/login")
    fun loginPage(): String {
        return "login"
    }

    @GetMapping("/changePassword")
    fun changePassword(model: Model): String {
        model.addAttribute("params", ParamsModel())

        return "changePassword"
    }

    @GetMapping("/about")
    fun about(): String {
        return "about"
    }

    @GetMapping("/admin")
    fun adminPage(model: Model): String {
        model.addAttribute("users", userRepository.findAllFileUsers().map { userMapper.toUserModel(it) })

        return "admin"
    }
}