package com.medialarma.app.data.mapper

import com.medialarma.app.data.remote.dto.UserDto
import com.medialarma.app.domain.model.User

fun UserDto.toDomain() = User(id = id, email = email, displayName = displayName)
