package com.hgm.plugins

import com.hgm.routes.*
import com.hgm.service.*
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()
    val commentService: CommentService by inject()

    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {
        //用户路由
        registerUser(userService)
        loginUser(userService, jwtAudience, jwtIssuer, jwtSecret)

        //关注路由
        followUser(followService)
        unfollowUser(followService)

        //帖子路由
        createPost(postService, userService)
        getPostsFromFollows(postService, userService)
        deletePost(postService, userService,likeService)

        //点赞路由
        likePost(likeService, userService)
        unlikePost(likeService, userService)

        //评论路由
        addComment(commentService, userService)
        deleteComment(commentService, userService, likeService)
        getCommentByPost(commentService)
    }
}
