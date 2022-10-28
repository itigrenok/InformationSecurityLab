package ru.stankin.dp.controller

import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.stankin.dp.dto.FileReaderMode
import ru.stankin.dp.dto.FileUser
import ru.stankin.dp.dto.UserModel
import ru.stankin.dp.mapper.UserMapper
import ru.stankin.dp.service.LoginService
import ru.stankin.dp.service.PasswordMaskService
import ru.stankin.dp.service.UserRepository

@Controller
class UserController(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val passwordMaskService: PasswordMaskService,
    private val loginService: LoginService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(UserController::class.java)
    }

    @GetMapping("/admin/updateUser/{userName}")
    fun updateUserPage(model: Model, @PathVariable userName: String): String {
        val fileUser = userRepository.findFileUser(userName)

        model.addAttribute("user", userMapper.toUserModel(fileUser!!))
        model.addAttribute("passwordMasks", passwordMaskService.getAllPasswordMask())

        return "updateUser"
    }

    @GetMapping("/admin/addUser")
    fun addUser(model: Model): String {
        model.addAttribute("user", UserModel())
        model.addAttribute("passwordMasks", passwordMaskService.getAllPasswordMask())

        return "addUser"
    }

    @PostMapping("/admin/updateUser")
    fun updateUser(userModel: UserModel): String {
        logger.info("Update user with: ${userModel.login}, ${userModel.roles}, ${userModel.blockAccount}, ${userModel.transportPassword}, ${userModel.limitationId}")

        val fileUser = userRepository.findFileUser(userModel.login!!)
        if (userModel.limitationId != fileUser?.limitationId) {
            userModel.transportPassword = true
        }

        userRepository.saveUser(userMapper.updateFileUser(userModel, fileUser!!), FileReaderMode.WRITE_UPDATE)

        return "redirect:/admin"
    }

    @PostMapping("/admin/addUser")
    fun addUser(userModel: UserModel): String {
        val user = FileUser(
            login = userModel.login,
            password = "user",
            roles = listOf("USER"),
            blockAccount = userModel.blockAccount,
            transportPassword = userModel.transportPassword,
            limitationId = userModel.limitationId
        )

        userRepository.saveUser(user, FileReaderMode.WRITE_NEW)
        loginService.addUser(user)

        return "redirect:/admin"
    }
}