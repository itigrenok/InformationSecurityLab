package ru.stankin.dp.dto

data class PasswordMask(
    var id: String? = null,
    var description: String? = null,
    var regex: List<String>? = null
)