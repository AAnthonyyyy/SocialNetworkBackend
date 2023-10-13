package com.hgm.plugins

import com.hgm.data.repository.user.UserRepository
import com.hgm.routes.loginUser
import com.hgm.routes.registerUser
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    routing {
        registerUser(userRepository)
        loginUser(userRepository)
    }
}
