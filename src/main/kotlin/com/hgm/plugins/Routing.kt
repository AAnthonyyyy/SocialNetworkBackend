package com.hgm.plugins

import com.hgm.data.repository.user.UserRepository
import com.hgm.routes.*
import com.hgm.service.FollowService
import com.hgm.service.PostService
import com.hgm.service.UserService
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()

    val userService:UserService by inject()
    val followService:FollowService by inject()
    val postService:PostService by inject()

    routing {
        //用户路由
        registerUser(userService)
        loginUser(userRepository)

        //关注路由
        followUser(followService)
        unfollowUser(followService)

        //帖子路由
        createPost(postService)
    }
}
