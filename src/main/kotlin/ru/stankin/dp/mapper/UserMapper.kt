package ru.stankin.dp.mapper

import org.springframework.stereotype.Component
import ru.stankin.dp.dto.FileUser
import ru.stankin.dp.dto.UserModel

@Component
class UserMapper {

    fun toUserModel(user: FileUser): UserModel {
        return UserModel(
            login = user.login,
            roles = user.roles,
            blockAccount = user.blockAccount,
            transportPassword = user.transportPassword,
            limitationId = user.limitationId
        )
    }

    fun updateFileUser(source: UserModel, target: FileUser): FileUser {
        return FileUser(
            login = source.login ?: target.login,
            password = target.password,
            roles = source.roles ?: target.roles,
            blockAccount = source.blockAccount,
            transportPassword = source.transportPassword,
            limitationId = source.limitationId ?: target.limitationId
        )
    }
}