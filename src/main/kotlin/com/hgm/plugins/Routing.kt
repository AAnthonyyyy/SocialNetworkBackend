package com.hgm.plugins

import com.hgm.data.repository.follow.FollowRepository
import com.hgm.data.repository.post.PostRepository
import com.hgm.data.repository.user.UserRepository
import com.hgm.routes.*
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository: FollowRepository by inject()
    val postRepository: PostRepository by inject()

    routing {
        //用户路由
        registerUser(userRepository)
        loginUser(userRepository)

        //关注路由
        followUser(followRepository)
        unfollowUser(followRepository)

        //帖子路由
        createPost(postRepository)
    }
}
