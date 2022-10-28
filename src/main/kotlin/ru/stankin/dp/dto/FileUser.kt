package ru.stankin.dp.dto

data class FileUser(
    var login: String? = null,
    var password: String? = null,
    var roles: List<String>? = null,
    var blockAccount: Boolean = false,
    var transportPassword: Boolean = true,
    var limitationId: String? = null
)