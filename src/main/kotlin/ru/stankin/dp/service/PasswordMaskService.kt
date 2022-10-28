package ru.stankin.dp.service

import org.springframework.stereotype.Service
import ru.stankin.dp.dto.PasswordMask

@Service
class PasswordMaskService {

    companion object {
        var passwordTypes = listOf(
            PasswordMask(
                id = "17",
                description = "Наличие букв, знаков препинания и знаков арифметических операций",
                regex = listOf(
                    "[\\w]+",
                    "[+\\-*/%]+",
                    "[,\\[\\]:!\\-\\(\\)\\.?;']+"
                )
            ),
            PasswordMask(
                id = "1",
                description = "Без ограничений",
                regex = listOf()
            )
        )
    }

    fun getAllPasswordMask(): List<PasswordMask> {
        return passwordTypes
    }

    fun findMaskById(id: String): PasswordMask {
        return passwordTypes.find { it.id == id }!!
    }
}