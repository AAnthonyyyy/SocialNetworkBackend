package com.hgm.plugins

import com.hgm.data.repository.follow.FollowRepository
import com.hgm.data.repository.user.UserRepository
import com.hgm.routes.followUser
import com.hgm.routes.loginUser
import com.hgm.routes.registerUser
import com.hgm.routes.unfollowUser
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository: FollowRepository by inject()
    routing {
        //用户路由
        registerUser(userRepository)
        loginUser(userRepository)

        //关注路由
        followUser(followRepository)
        unfollowUser(followRepository)
    }
}
