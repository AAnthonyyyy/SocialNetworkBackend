package com.hgm.plugins

import com.hgm.repository.user.UserRepository
import com.hgm.routes.userRoutes
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository:UserRepository by inject()
    routing {
        userRoutes(userRepository)
    }
}
