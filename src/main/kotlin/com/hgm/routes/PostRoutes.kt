package com.hgm.routes

import com.hgm.data.requests.CreatePostRequest
import com.hgm.data.requests.DeletePostRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.service.LikeService
import com.hgm.service.PostService
import com.hgm.service.UserService
import com.hgm.utils.ApiResponseMessage
import com.hgm.utils.Constants
import com.hgm.utils.QueryParams
import com.hgm.utils.ifEmailBelongsToUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createPost(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        post("/api/post/create") {
            val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            ifEmailBelongsToUser(
                userId = request.userId,
                onValidateEmail = userService::doesEmailBelongToUserId
            ) {
                // TODO("后期看看没有没必要在创建帖子之前要查询用户是否存在")
                val doesUserExist = postService.createPost(request)
                if (!doesUserExist) {
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            successful = false,
                            message = ApiResponseMessage.USER_NOT_FOUND
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            successful = true,
                            message = ApiResponseMessage.CREATE_POST_SUCCESSFUL
                        )
                    )
                }
            }
        }
    }
}


fun Route.getPostsFromFollows(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        get("/api/post/posts") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val page =
                call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            ifEmailBelongsToUser(
                userId = userId,
                onValidateEmail = userService::doesEmailBelongToUserId
            ) {
                val posts = postService.getPostsFromFollows(userId, page, pageSize)
            }
        }
    }
}


fun Route.deletePost(
    postService: PostService,
    userService: UserService,
    likeService: LikeService
) {
    delete("/api/post/delete") {
        val request = call.receiveOrNull<DeletePostRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@delete
        }

        val post = postService.getPost(request.postId)
        if (post == null) {
            call.respond(HttpStatusCode.NotFound)
            return@delete
        }

        ifEmailBelongsToUser(
            userId = post.userId,
            onValidateEmail = userService::doesEmailBelongToUserId
        ) {
            //删除帖子也要把关于帖子的点赞以及评论删除
            postService.deletePost(request.postId)
            likeService.removeLikeByParent(request.postId)
            // TODO: 删除评论
            call.respond(
                HttpStatusCode.OK,
                BaseResponse(
                    successful = true,
                    message = ApiResponseMessage.DELETE_POST_SUCCESSFUL
                )
            )
        }
    }
}

