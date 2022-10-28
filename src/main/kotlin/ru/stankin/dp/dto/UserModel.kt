package ru.stankin.dp.dto

data class UserModel(
    var login: String? = null,
    var roles: List<String>? = null,
    var blockAccount: Boolean = false,
    var transportPassword: Boolean = true,
    var limitationId: String? = null
)