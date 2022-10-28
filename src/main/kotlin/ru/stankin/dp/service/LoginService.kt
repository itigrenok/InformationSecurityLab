package ru.stankin.dp.service

import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.stereotype.Service
import ru.stankin.dp.dto.FileReaderMode
import ru.stankin.dp.dto.FileUser

@Service
class LoginService(
    private val inMemoryUserDetailsManager: InMemoryUserDetailsManager,
    private val passwordEncoder: PasswordEncoder,
    private val fileReader: UserRepository,
    private val passwordMaskService: PasswordMaskService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(LoginService::class.java)
    }

    fun addUser(user: FileUser) {
        inMemoryUserDetailsManager.createUser(
            User.withUsername(user.login)
                .password(passwordEncoder.encode(user.password))
                .accountLocked(user.blockAccount)
                .roles(*user.roles!!.toTypedArray())
                .build()
        )

        logger.info("Add user {}", user.login)
    }

    fun changePassword(login: String, oldPassword: String, firstNewPassword: String, secondNewPassword: String): Boolean {
        if (inMemoryUserDetailsManager.userExists(login)) {
            val user = inMemoryUserDetailsManager.loadUserByUsername(login)
            val fileUser = fileReader.findFileUser(login)
            val mask = fileUser?.limitationId?.let { passwordMaskService.findMaskById(it) }

            when {
                !passwordEncoder.matches(oldPassword, user.password) -> throw Exception("Неверно указан старый пароль")
                firstNewPassword != secondNewPassword -> throw Exception("Новые пароли не совпадают")
                mask != null && !mask.regex!!.all { !it.toRegex().find(firstNewPassword)?.value.isNullOrBlank() } -> throw Exception("Пароль должен соблюдать условие: ${mask.description}")
            }

            inMemoryUserDetailsManager.updateUser(
                User.withUserDetails(user)
                    .password(passwordEncoder.encode(firstNewPassword))
                    .build()
            )

            fileReader.saveUser(
                user = FileUser(
                    login = login,
                    password = firstNewPassword,
                    roles = fileUser?.roles,
                    blockAccount = false,
                    transportPassword = false,
                    limitationId = fileUser?.limitationId
                ),
                mode = FileReaderMode.WRITE_UPDATE
            )

            return fileUser?.transportPassword == true
        }

        return false
    }
}