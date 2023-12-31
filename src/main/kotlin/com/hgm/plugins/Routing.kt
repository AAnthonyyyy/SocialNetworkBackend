package com.hgm.plugins

import com.hgm.routes.*
import com.hgm.service.*
import com.hgm.service.chat.ChatController
import com.hgm.service.chat.ChatService
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()
    val commentService: CommentService by inject()
    val activityService: ActivityService by inject()
    val skillService: SkillService by inject()
    val chatService: ChatService by inject()
    val chatController: ChatController by inject()

    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {
        //身份验证路由
        authenticate()
        registerUser(userService)
        loginUser(userService, jwtAudience, jwtIssuer, jwtSecret)

        //用户路由
        searchUser(userService)
        getUserProfile(userService)
        getPostsForProfile(postService)
        updateUserProfile(userService)

        //关注路由
        followUser(followService, activityService)
        unfollowUser(followService)

        //帖子路由
        getPostDetail(postService)
        createPost(postService)
        getPostsForFollows(postService)
        deletePost(postService, likeService, commentService)

        //点赞路由
        likeParent(likeService, activityService)
        unlikeParent(likeService)
        getLikesForParent(likeService)

        //评论路由
        addComment(commentService, activityService)
        deleteComment(commentService, likeService)
        getCommentForPost(commentService)

        //活动路由
        getActivities(activityService)

        //技巧
        getSkills(skillService)

        //聊天
        getChatsForUser(chatService)
        getMessagesForChat(chatService)
        chatWebSocket(chatController)

        //静态路由用于显示资源图片
        static {
            resources("static")
        }
    }
}
