package ru.stankin.dp.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import ru.stankin.dp.dto.FileReaderMode
import ru.stankin.dp.dto.FileUser

@Component
class UserRepository(
    private val passwordEncoder: PasswordEncoder,
    private val fileService: FileService
) {

    companion object {
        private val objectMapper = ObjectMapper()
    }

    fun findFileUser(login: String): FileUser? {
        return userData().find { it.login == login }
    }

    fun findAllFileUsers(): List<FileUser> {
        return userData()
    }

    fun findUserDetails(): List<UserDetails> {
        return userData().map {
            User.withUsername(it.login!!)
                .password(passwordEncoder.encode(it.password!!))
                .roles(*it.roles!!.toTypedArray())
                .accountLocked(it.blockAccount)
                .build()
        }
    }

    fun saveUser(user: FileUser, mode: FileReaderMode) {
        when (mode) {
            FileReaderMode.WRITE_UPDATE -> {
                val jsonNode = userDataTree()

                (jsonNode.find { it.findValue("login").textValue() == user.login } as ObjectNode).apply {
                    put("password", user.password)
                    put("blockAccount", user.blockAccount)
                    put("transportPassword", user.transportPassword)
                    put("limitationId", user.limitationId)
                }

                objectMapper.writeValue(fileService.openFile(), jsonNode)
            }
            FileReaderMode.WRITE_NEW -> {
                val jsonNode = userDataTree()
                val objectNode = objectMapper.createObjectNode()

                objectNode.put("login", user.login)
                objectNode.put("password", user.password)
                objectNode.put("roles", objectMapper.createArrayNode().apply {
                    user.roles?.forEach { add(it) }
                })
                objectNode.put("blockAccount", user.blockAccount)
                objectNode.put("transportPassword", user.transportPassword)
                objectNode.put("limitationId", user.limitationId)

                objectMapper.writeValue(fileService.openFile(), (jsonNode as ArrayNode).add(objectNode))
            }
        }
    }

    private fun userData(): List<FileUser> {
        return fileService.openDataFile()?.run { objectMapper.readValue(this) } ?: emptyList()
    }

    private fun userDataTree(): JsonNode {
        return objectMapper.readTree(fileService.openDataFile())
    }
}