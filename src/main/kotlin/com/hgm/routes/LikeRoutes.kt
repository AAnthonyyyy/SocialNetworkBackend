package com.hgm.routes

import com.hgm.data.requests.LikeRequest
import com.hgm.data.responses.BaseResponse
import com.hgm.service.LikeService
import com.hgm.service.PostService
import com.hgm.service.UserService
import com.hgm.utils.ApiResponseMessage
import com.hgm.utils.ifEmailBelongsToUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.likePost(
    likeService: LikeService,
    userService: UserService
) {
    authenticate {
        post("/api/like/like_post") {
            val request = call.receiveOrNull<LikeRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            ifEmailBelongsToUser(
                userId = request.userId,
                onValidateEmail = userService::doesEmailBelongToUserId
            ) {
                val likeSuccessful = likeService.likePost(request.userId, request.parentId)
                if (likeSuccessful) {
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            successful = true,
                            message = ApiResponseMessage.LIKE_POST_SUCCESSFUL
                        )
                    )
                }else{
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            successful = false,
                            message = ApiResponseMessage.USER_NOT_FOUND
                        )
                    )
                }
            }
        }
    }
}


fun Route.unlikePost(
    likeService: LikeService,
    userService: UserService,
) {
    authenticate {
        delete("/api/like/unlike_post") {
            val request = call.receiveOrNull<LikeRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            ifEmailBelongsToUser(
                userId = request.userId,
                onValidateEmail = userService::doesEmailBelongToUserId
            ) {
                val unlikeSuccessful = likeService.unlikePost(request.userId, request.parentId)
                if (unlikeSuccessful) {
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            successful = true,
                            message = ApiResponseMessage.LIKE_POST_SUCCESSFUL
                        )
                    )
                }else{
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            successful = false,
                            message = ApiResponseMessage.USER_NOT_FOUND
                        )
                    )
                }
            }
        }
    }
}