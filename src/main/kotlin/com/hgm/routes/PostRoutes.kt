package com.hgm.routes

import com.hgm.data.requests.CreatePostRequest
import com.hgm.data.requests.DeletePostRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.service.CommentService
import com.hgm.service.LikeService
import com.hgm.service.PostService
import com.hgm.service.UserService
import com.hgm.utils.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createPost(
    postService: PostService,
) {
    authenticate {
        post("/api/post/create") {
            val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val doesUserExist = postService.createPost(request,call.userId)
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


fun Route.getPostsFromFollows(
    postService: PostService,
) {
    authenticate {
        get("/api/post/get") {
            val page =
                call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE


            val posts = postService.getPostsFromFollows(call.userId, page, pageSize)
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}


fun Route.deletePost(
    postService: PostService,
    likeService: LikeService,
) {
    authenticate {
        delete("/api/post/delete") {
            val request = call.receiveOrNull<DeletePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val post = postService.getPost(request.postId) ?: kotlin.run {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            if (call.userId == post.userId) {
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
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}

